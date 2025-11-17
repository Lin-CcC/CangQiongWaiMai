package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        //5. 将订单数据添加到数据库中
        orderMapper.add(orders);
        log.info("订单号id为：{}", orders.getId());

        //添加购物车数据到订单明细表
        //1. 将购物车列表里面的数据对照订单明细表的字段填充完整
        List<OrderDetail> orderDetailList = new ArrayList<>();
        Long orderId = orders.getId();
        OrderDetail orderDetail = new OrderDetail();
        for (ShoppingCart sc:
             shoppingCartList) {
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

}
