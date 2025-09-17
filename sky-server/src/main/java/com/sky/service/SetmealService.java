package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void add(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO selectById(Long id);

    void changeStatus(Integer status, Long id);

    void modifyWithDish(SetmealDTO setmealDTO);

    void deleteWithDish(List<Long> ids);
}
