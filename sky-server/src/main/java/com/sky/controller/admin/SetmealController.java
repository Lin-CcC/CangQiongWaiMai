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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    //@CacheEvict(cacheNames = "setmealCache", key = "setmealDTO.categoryId")
    //这里跟菜品一样，因为套餐刚添加的时候是禁用状态，禁用状态的套餐不会显示在界面上，只有把套餐修改为起售状态时，我们才需要对套餐的缓存进行清空
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

    //这个查询操作是后端的搜索框的查询操作，不是小程序那边发起的查询操作，所以不需要对缓存进行操作
    @GetMapping("/{id}")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        log.info("查询id为{}的套餐", id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }

    //非常核心的清理缓存
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status, Long id){
        log.info("更改id为{}的状态：{}", id, status);
        setmealService.changeStatus(status, id);
        return Result.success();
    }

    //修改一个套餐，它可能在多个分类下都存在，所以我们需要删除所有的分类缓存，是不是起售状态都可以被修改，虽然不是起售状态的套餐修改了不会对界面造成影响，但是由于判断不方便，我们这里把它全部概括为删除
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @PutMapping
    public Result modifyWithDish(@RequestBody SetmealDTO setmealDTO){
        setmealService.modifyWithDish(setmealDTO);
        return Result.success();
    }

    //@CacheEvict(cacheNames = "setmealCache", allEntries = true)
    //删除本身也不需要清理缓存，因为在删除之前，这个菜品要先被停售，这个时候就已经清理了一次缓存了，此时这个菜品就不需要显示到页面上了，对这个停售的菜品进行任何操作都不重要，删除也不会造成影响
    @DeleteMapping
    public Result deleteWithDish(@RequestParam List<Long> ids){
        log.info("需要删除的套餐的id为：{}", ids);
        setmealService.deleteWithDish(ids);
        return Result.success();
    }
}
