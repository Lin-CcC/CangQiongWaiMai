package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Transactional
    @Override
    public void addWithFlavour(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.add(dish);
        log.info("插入了菜品：{}", dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long dishId = dish.getId();
        log.info("味道相关内容：{}", flavors);
        if (flavors.size() > 0 && flavors != null){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    @Transactional
    public void delete(String ids) {
        long[] id = Arrays.stream(ids.split(",")).mapToLong(Long::parseLong).toArray();
        int count = dishMapper.statusHasOn(id);
        if (count > 0){
            log.info("其中含有起售的菜品个数：{}, 所以删除失败", count);
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        count = setmealDishMapper.hasMealConnect(id);
        if (count > 0){
            log.info("其中含有连接的菜品个数：{}, 所以删除失败", count);
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishFlavorMapper.deleteByIds(id);
        dishMapper.deleteByIds(id);
        log.info("删除成功了！id为{}", id);
    }

    @Override
    public List<Dish> selectById(String id) {
        return dishMapper.selectByCategoryId(id);
    }

    @Override
    public DishVO selectById(Long id) {
        return dishMapper.selectById(id);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        dishMapper.changeStatus(status, id);
    }

    @Override
    @Transactional
    public void modify(DishDTO dishDTO) {
        List<DishFlavor> flavors = dishDTO.getFlavors();
        long[] id = new long[1];
        id[0] = dishDTO.getId();
        dishFlavorMapper.deleteByIds(id);
        if (flavors != null && flavors.isEmpty() != true){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.modify(dish);
    }

    @Override
    public List<DishVO> list(Long categoryId) {
        List<DishVO> dishVOList = dishMapper.list(categoryId);
        return dishVOList;
    }

}
