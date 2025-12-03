package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;
    @Value("${sky.shop.address}")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;

    private static String URL = "https://api.map.baidu.com/geocoding/v3/?";
    private static String RIDE_URL = "https://api.map.baidu.com/directionlite/v1/riding?";

    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //传入的内容：地址簿id，总金额，配送状态，预计送达时间，打包费，付款方式，备注，餐具数量，餐具数量状态
        //返回的内容：订单id，订单金额，下单时间，订单号
        //0. 报错检查
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        log.info("填充前的订单信息为：{}", orders);
        //用户id
        Long userId = BaseContext.getCurrentId();
        //地址簿
        AddressBook addressBook = addressBookMapper.selectById(orders.getAddressBookId(), userId);
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.select(shoppingCart);
        if (shoppingCartList.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //1. 存储订单内容：
        //2. 填充冗余字段（手机号，详细地址信息）以及用户id
        orders.setUserId(userId);
        //详细地址信息
        orders.setAddress(addressBook.getDetail());

        //手机号
        orders.setPhone(addressBook.getPhone());
        //用户名等
        orders.setConsignee(addressBook.getConsignee());
        //3. 设置订单状态为待付款，支付状态为未支付
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        //4. 设置订单号(时间戳+userId)
        orders.setNumber(String.valueOf(System.currentTimeMillis()) + userId);
        //时间字段
        orders.setOrderTime(LocalDateTime.now());
        log.info("填充完毕的订单信息为：{}", orders);
        if (!checkOutRange(orders.getAddress())){
            throw new OrderBusinessException("超出了5km的配送距离，不给你配送");
        };
        //5. 将订单数据添加到数据库中
        orderMapper.add(orders);
        log.info("订单号id为：{}", orders.getId());

        //添加购物车数据到订单明细表
        //1. 将购物车列表里面的数据对照订单明细表的字段填充完整
        List<OrderDetail> orderDetailList = new ArrayList<>();
        Long orderId = orders.getId();

        for (ShoppingCart sc:
             shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(sc, orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.addBatch(orderDetailList);

        //删除购物车里的内容
        shoppingCartMapper.delete(shoppingCart);
        //6. 返回一个OrderSubmitVO
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .build();
        log.info("返回的订单对象的值为：{}", orderSubmitVO);
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersCancelDTO, orders);
        orders.setStatus(Orders.CANCELLED);
        orderMapper.changeStatus(orders);
    }

    @Override
    public void cancelOrder(long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.CANCELLED);
        orderMapper.changeStatus(orders);
    }

    @ParameterizedTest
    @ValueSource(strings = {"北京市海淀区上地十街10号"}) // 测试用的地址参数
    @Override
    public void checkOutOfRange(String address) throws IOException {
        //将传入地址的相关参数封装进一个map对象里
        String ak = "M1DpwBM3sbGfuZpoQquf0cpHmnUA2V4u";
        Map<String, String> params = new HashMap<>();
        params.put("address", address);
        params.put("output", "json");
        params.put("ak", ak);
        params.put("callback", "showLocation");
        System.out.println(address);
        System.out.println(ak);
        if (URL == null || URL.length() <= 0 || params == null || params.size() <= 0){
            return;
        }

        StringBuffer queryString = new StringBuffer();
        queryString.append(URL);
        for (Map.Entry<?, ?> pair : params.entrySet()){
            queryString.append(pair.getKey() + "=");
            queryString.append(UriUtils.encode((String)pair.getValue(), "UTF-8") + "&");
        }
        if (queryString.length() > 0){
            queryString.deleteCharAt(queryString.length() - 1);
        }
        java.net.URL url = new URL(queryString.toString());
        System.out.println(queryString.toString());
        URLConnection httpConnection = (URLConnection) url.openConnection();
        httpConnection.connect();

        InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null){
            buffer.append(line);
        }
        reader.close();
        isr.close();
        System.out.println("AK = " + buffer.toString());
    }

    private Boolean checkOutRange(String address){
        if (address == null || ak == null || shopAddress == null){
            throw new BaseException("参数没有填写完整，无法调用api");
        }
        //封装请求参数
        Map<String, String> requestParam = new HashMap<>();
        //发送的是用户地址的请求参数
        requestParam.put("address", address);
        requestParam.put("ak", ak);
        requestParam.put("output", "json");
        //拼接url
        //url：https://api.map.baidu.com/geocoding/v3/?address=北京市海淀区上地十街10号&output=json&ak=您的ak&callback=showLocation
        //GET请求
        //当前URL=https://api.map.baidu.com/geocoding/v3/?
//        StringBuffer sb = new StringBuffer(URL);
//        for (Map.Entry<String, String> entrySet :
//                requestParam.entrySet()) {
//            String key = entrySet.getKey();
//            String value = entrySet.getValue();
//            sb.append(key).append("=").append(value).append("&");
//        }
//        //删去url中的最后一个&
//        sb.deleteCharAt(sb.length() - 1);
//        //得到url的String
//        String url = sb.toString();
        //调用api,这里只需要对api传回来的数据进行处理就可以了，不用自己处理链接
        String result = HttpClientUtil.doGet(URL, requestParam);
        //处理数据
        JSONObject jsonObject = JSON.parseObject(result);
        Integer status = jsonObject.getInteger("status");
        if (status != 0){
            throw new BaseException("第一次调用api失败");
        }
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        Float userLng = location.getFloat("lng");
        Float userLat = location.getFloat("lat");

        requestParam.put("address", shopAddress);
        result = HttpClientUtil.doGet(URL, requestParam);
        jsonObject = JSON.parseObject(result);
        status = jsonObject.getInteger("status");
        if (status != 0){
            throw new BaseException("第二次调用api失败");
        }
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        Float adminLng = location.getFloat("lng");
        Float adminLat = location.getFloat("lat");

        log.info("店家的经纬度分别为：{}， {}", adminLng, adminLat);
        log.info("用户的经纬度分别为：{}， {}", userLng, userLat);

        Map params = new LinkedHashMap<String, String>();
        String origin = userLat + "," + userLng;
        String destination = adminLat + "," + adminLng;
        params.put("origin", origin);
        params.put("destination", destination);
        params.put("ak", ak);
        log.info("origin: {}, destination: {}", origin, destination);
        //调用api
        String rideResult = HttpClientUtil.doGet(RIDE_URL, params);
        jsonObject = JSON.parseObject(rideResult);
        status = jsonObject.getInteger("status");
        if (status != 0){
            log.info("第三次调用api的status的值为：{}", status);
            throw new BaseException("第三次调用api失败");
        }
        JSONArray routes = jsonObject.getJSONObject("result").getJSONArray("routes");
        JSONObject routesObject = routes.getJSONObject(0);
        Float distance = routesObject.getFloat("distance");
        Float duration = routesObject.getFloat("duration");
        //验证两地之间的距离

        log.info("两地之间的距离为：{}， 两地之间骑车需要的时间大约为：{}", distance, duration);
        //判断两地之间的距离是否小于5000m
        return distance <= 5000;
    }



    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        List<OrderVO> list = orderMapper.conditionSearch(ordersPageQueryDTO);
        long total = orderMapper.countOrders(ordersPageQueryDTO);
        log.info("list的内容：{}", list);

        // 直接遍历每个订单，无需额外维护orderDetailList
        for (OrderVO orderVO : list) {
            List<OrderDetail> odList = orderVO.getOrderDetailList();
            // 处理空订单项，避免空指针
            if (odList == null || odList.isEmpty()) {
                orderVO.setOrderDishes("");
                continue;
            }

            // 统计商品名称与数量（累加实际购买数量，而非固定+1）
            Map<String, Integer> dishCountMap = new HashMap<>();
            for (OrderDetail od : odList) {
                String dishName = od.getName();
                // 存在则累加数量，不存在则初始化为当前订单项的数量
                dishCountMap.put(
                        dishName,
                        dishCountMap.getOrDefault(dishName, 0) + od.getNumber()
                );
            }

            // 用StringBuilder高效拼接字符串
            StringBuilder orderDishesSb = new StringBuilder();
            for (Map.Entry<String, Integer> entry : dishCountMap.entrySet()) {
                orderDishesSb.append(entry.getKey())
                        .append("*")
                        .append(entry.getValue())
                        .append(";");
            }

            // 移除最后一个多余的分号（若有内容）
            String orderDishes = orderDishesSb.toString();
            if (!orderDishes.isEmpty()) {
                orderDishes = orderDishes.substring(0, orderDishes.length() - 1);
            }

            // 设置到当前订单
            orderVO.setOrderDishes(orderDishes);
        }

        //拿到了每一个orderVO里的orderDetail列表
        //按照订单，一个个遍历OrderDetailList，然后把对应的orderDtail放到map里面进行数据的更新
            //填充完毕，然后把这个map里的内容拼接为字符串
            //dish字段= “name” + “*” + “数量” + “；”
        //在把list查出来的时候就把里面的dish字段给填充了
        //一共有四部分组成，其中，需要手动获取的就是name和数量
        //数量= list中OrderVO里的OrderDetailList的对应商品的数量，在相应名字上+1
        //name= 遍历list中OrderVO里的name，如果没有就存下来，有的话就不存下来
        //循环的方式，先拿到name，然后在统计数量，使用数组，好像hashmap更合适，这里使用哈希map
        //填充关于菜品的String字符串

        return new PageResult(total, list);
    }

    @Override
    public List<OrderStatisticsVO> statisticOrder() {
        List<OrderStatisticsVO> list = orderMapper.statisticOrder();
        log.info("各个状态的订单数量统计结果为：{}", list);
        return list;
    }

    @Override
    public OrderVO orderDetail(long id) {
//        log.info("这个ak的值是：{}", ak);
        OrderVO orderVO = orderMapper.orderDetailById(id);
        return orderVO;
    }

    @Override
    public void repetitionOrder(long id) {
        //拿到这个id对应的订单数据，更改状态，然后插入订单数据，返回order的id
        List<OrderDetail> orderDetailList = orderDetailMapper.selectById(id);
        for (OrderDetail od :
                orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(od, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.add(shoppingCart);
        }
        //拿到这个id对应的订单明细数据，然后更改orderId和状态，插入订单明细表
    }

    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersRejectionDTO, orders);
        orders.setStatus(Orders.CANCELLED);
        orderMapper.changeStatus(orders);
    }

    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.changeStatus(orders);
    }

    @Override
    public void deliveryOrder(long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.changeStatus(orders);
    }

    @Override
    public void completeOrder(long id) {
//        log.info("这个ak的值是：{}", ak);
        Orders orders = Orders.builder().id(id).status(Orders.COMPLETED).build();
        orderMapper.changeStatus(orders);
    }

}
