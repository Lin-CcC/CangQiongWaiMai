package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(operationType = OperationType.INSERT)
    void add(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    int statusHasOn(long[] id);

    @Select("select dish.*, dish_flavor.id as flavor_id, dish_flavor.name as flavor_name, dish_flavor.dish_id, dish_flavor.value as flavor_value from dish left outer join dish_flavor on dish.id = dish_flavor.dish_id where dish.id = #{id}")
    @ResultMap("DishVOResultMap")
    DishVO selectById(Long id);

    @Update("update dish set status = #{status} where id = #{id}")
    void changeStatus(Integer status, Long id);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByCategoryId(String categoryId);

    void deleteByIds(long[] id);

    @AutoFill(operationType = OperationType.UPDATE)
    void modify(Dish dish);

    @Select("select dish.*, dish_flavor.id as flavor_id, dish_flavor.name as flavor_name, dish_flavor.value as flavor_value, dish_flavor.dish_id from dish left outer join dish_flavor on dish.id = dish_flavor.dish_id where dish.category_id = #{categoryId}")
    @ResultMap("DishVOResultMap")
    List<DishVO> list(Long categoryId);
}
