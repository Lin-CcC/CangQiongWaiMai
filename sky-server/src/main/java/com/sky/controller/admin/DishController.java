package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    @PostMapping
    public Result addWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.addWithFlavour(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result delete(String ids){
        log.info("需要删除的id为：{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> selectByCategoryId(String categoryId){
        List<Dish> dish = dishService.selectById(categoryId);
        log.info("id为{}，列表为{}", categoryId, dish);
        return Result.success(dish);
    }

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id){
        dishService.changeStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> selectById(@PathVariable Long id){
        DishVO dish = dishService.selectById(id);
        log.info("回显的参数为：{}", dish);
        return Result.success(dish);
    }

    @PutMapping
    public Result modify(@RequestBody DishDTO dishDTO){
        log.info("修改后的菜品数据应该为：{}", dishDTO);
        dishService.modify(dishDTO);
        return Result.success();
    }
}
