package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.MessageConstant;
import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AdminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {
    @Autowired
    OrderService orderService;

    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }


    @GetMapping("/statistics")
    public Result<List<OrderStatisticsVO>> statisticOrder(){
        List<OrderStatisticsVO> list = orderService.statisticOrder();
        return Result.success(list);
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询传入的参数为：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
//        if (pageResult.getTotal() == 0)
//            log.info("进来了");
//            return Result.success(pageResult, MessageConstant.ORDER_NOT_FOUND);
        return Result.success(pageResult);
    }

    @GetMapping("details/{id}")
    public Result<OrderVO> orderDetail(@PathVariable long id){
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/rejection")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/confirm")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable long id){
        orderService.deliveryOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable long id){
        orderService.completeOrder(id);
        return Result.success();
    }
}
