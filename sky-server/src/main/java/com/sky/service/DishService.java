package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {
    void addWithFlavour(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void delete(String ids);

    List<Dish> selectById(String id);

    DishVO selectById(Long id);

    void changeStatus(Integer status, Long id);

    void modify(DishDTO dishDTO);
}
