package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        return Result.success(shoppingCartService.list());
    }

    @PostMapping("/sub")
    public Result reduceNumber(@RequestBody  ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.reduceNumber(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    public Result cleanShoppingCart(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}
