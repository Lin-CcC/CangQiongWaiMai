package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @PostMapping
    public Result add(@RequestBody SetmealDTO setmealDTO){
        log.info("添加套餐：{}", setmealDTO);
        setmealService.add(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        log.info("查询id为{}的套餐", id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id){
        log.info("更改id为{}的状态：{}", id, status);
        setmealService.changeStatus(status, id);
        return Result.success();
    }

    @PutMapping
    public Result modifyWithDish(@RequestBody SetmealDTO setmealDTO){
        setmealService.modifyWithDish(setmealDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteWithDish(@RequestParam List<Long> ids){
        log.info("需要删除的套餐的id为：{}", ids);
        setmealService.deleteWithDish(ids);
        return Result.success();
    }
}
