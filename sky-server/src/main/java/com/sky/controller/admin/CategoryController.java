package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PutMapping
    public Result modifyCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("传入的参数为：{}", categoryDTO);
        categoryService.modifyCategory(categoryDTO);
        return Result.success();
    }

    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("添加的种类为：{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    @DeleteMapping()
    public Result deleteById(int id){
        log.info("需要删除的分类id为：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Category>> searchCategoryByType(int type){
        log.info("需要查找的分类的类型为：{}", type);
        List<Category> category = categoryService.searchCategoryByType(type);
        return Result.success(category);
    }

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable int status, int id){
        log.info("更改的id为{}，状态为{}",id,status);
        categoryService.changeStatus(status, id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("传入的分页查询参数：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.categoryPageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

}
