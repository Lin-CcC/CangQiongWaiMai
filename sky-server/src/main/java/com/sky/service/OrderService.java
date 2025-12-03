package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    List<OrderStatisticsVO> statisticOrder();

    OrderVO orderDetail(long id);

    void repetitionOrder(long id);

    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void deliveryOrder(long id);

    void completeOrder(long id);

    void cancelOrder(long id);

    void checkOutOfRange(String address) throws IOException;
}
