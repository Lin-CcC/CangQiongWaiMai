package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;

import java.util.List;

public interface CategoryService {
    PageResult categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void modifyCategory(CategoryDTO categoryDTO);

    void changeStatus(int status, long id);

    void addCategory(CategoryDTO categoryDTO);

    void deleteById(long id);

    List<Category> searchCategoryByType(int type);
}
