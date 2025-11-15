package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

//    @AutoFill(operationType = OperationType.INSERT)
    @Insert("insert into shopping_cart(id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "values" +
            "(#{id}, #{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void add(ShoppingCart shoppingCart);
//  使用动态sql实现冗余字段填充功能并不合适
//    ShoppingCart fullInfo(ShoppingCart shoppingCart);

    List<ShoppingCart> select(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number}, create_time = #{createTime} where id = #{id}")
    void updateNumberAndTime(ShoppingCart shoppingCart);

    void delete(ShoppingCart shoppingCart);

    void updateNumber(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void cleanAll(long userId);
}
