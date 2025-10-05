package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    DishService dishService;

    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        List<DishVO> dishVOList = dishService.list(categoryId);
        log.info("当前菜品的列表为：{}", dishVOList);
        return Result.success(dishVOList);
    }
}
