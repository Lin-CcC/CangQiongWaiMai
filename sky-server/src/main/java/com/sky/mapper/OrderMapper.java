package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    Long add(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    // 1. 分页查询：先查订单表分页，再关联订单项
    List<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    // 2. 统计总条数：只查符合条件的订单数（主表统计，避免重复）
    Long countOrders(OrdersPageQueryDTO dto);

    @Select("select " +
            "count(case when status = 2 then id end) as toBeConfirmed, " +
            "count(case when status = 3 then id end) as confirmed, " +
            "count(case when status = 4 then id end) as deliveryInProgress " +
            "from orders")
    List<OrderStatisticsVO> statisticOrder();

    OrderVO orderDetailById(long id);
    void changeStatus(Orders orders);

    @Select("select * from orders where id = #{id}")
    Orders selectById(long id);
}
