package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;
import java.util.Set;

public interface SetmealService {
    void add(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO selectById(Long id);

    void changeStatus(Integer status, Long id);

    void modifyWithDish(SetmealDTO setmealDTO);

    void deleteWithDish(List<Long> ids);

    List<Setmeal> list(long categoryId);

    List<DishItemVO> selectBySetmealId(Long id);
}
