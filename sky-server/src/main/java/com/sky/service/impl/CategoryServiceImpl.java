package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 删除分类，如果关联了菜品或者套餐就抛出异常
     * @param id
     */
    @Override
    public void deleteById(long id) {
        Integer count = dishMapper.countByCategoryId(id);
        if (count != 0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        count = setmealMapper.countByCategoryId(id);
        if (count != 0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        categoryMapper.deleteById(id);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.categoryPageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> result = page.getResult();
        return new PageResult(total, result);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void modifyCategory(CategoryDTO categoryDTO) {
        //
        Category category = new Category();
       BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.modifyCategory(category);
    }

    /**
     * 更改状态
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(int status, long id) {
        //
        Category category = Category.builder().status(status).id(id).build();
        categoryMapper.changeStatus(category);
    }

    /**
     * 添加分类
     * @param categoryDTO
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
//                .createTime(LocalDateTime.now())
//                        .updateTime(LocalDateTime.now())
//                                .createUser(BaseContext.getCurrentId())
//                                        .updateUser(BaseContext.getCurrentId())
                                                .status(0)
                                                        .build();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.addCategory(category);
    }

    /**
     * 根据类型查找分类
     * @param type
     * @return
     */
    @Override
    public List<Category> searchCategoryByType(int type) {
        Category category = Category.builder().type(type).build();
        return categoryMapper.searchCategory(category);
    }

    @Override
    public List<Category> list(Integer type) {
        Category category = Category.builder().type(type).build();
        return categoryMapper.searchCategory(category);
    }
}
