package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        log.info("填充之前的购物车内容为：{}", shoppingCartDTO);
        //获取用户id，复制信息
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断购物车是否已经有相关数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.select(shoppingCart);
        log.info("现在查询该用户的购物车信息(null就是没有信息)：{}", shoppingCartList);
        if (shoppingCartList != null && shoppingCartList.size() > 0){
            shoppingCart = shoppingCartList.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.updateNumberAndTime(shoppingCart);
            return;
        }else {
            //判断需要插入的数据是谁的，填充冗余字段
//        shoppingCart = shoppingCartMapper.fullInfo(shoppingCart);
            if (shoppingCart.getDishId() != null) {
                //这是一个关于餐点的冗余数据段填充
                DishVO dishVO = dishMapper.selectById(shoppingCart.getDishId());
                shoppingCart.setImage(dishVO.getImage());
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setName(dishVO.getName());
            } else if (shoppingCart.getSetmealId() != null) {
                //这是一个关于套餐的冗余数据填充
                SetmealVO setmealVO = setmealMapper.select(shoppingCart.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setImage(setmealVO.getImage());
            }
            //填充两者的统一字段： 数量为1
            shoppingCart.setNumber(1);
        }
        //更新或填充最新时间
        shoppingCart.setCreateTime(LocalDateTime.now());
        log.info("填充完毕的购物车内容为：{}",shoppingCart);
        shoppingCartMapper.add(shoppingCart);
    }

    @Override
    public List<ShoppingCart> list() {
        //拿到当前用户的id
        long id = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(id).build();
         List<ShoppingCart> shoppingCartList = shoppingCartMapper.select(shoppingCart);
         log.info("当前用户的购物车信息为：{}", shoppingCartList);
        return shoppingCartList;
    }

    @Override
    public void reduceNumber(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //拿到用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //查询对应菜品的信息，确认数量
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.select(shoppingCart);
        shoppingCart = shoppingCartList.get(0);
        Integer number = shoppingCart.getNumber();
        int number_i = number.intValue();
        //对数量进行修改，如果现在的数量为1，那么就直接从菜品中删除这条记录
        if (number_i == 1){
            shoppingCartMapper.delete(shoppingCart);
        }else {
            //如果现在数量大于1，那么修改数量update
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateNumber(shoppingCart);
        }

    }

    @Override
    public void cleanShoppingCart() {
        long userId = BaseContext.getCurrentId();
        shoppingCartMapper.cleanAll(userId);
    }
}
