package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where id = #{id}")
    Integer countByCategoryId(long id);

    @AutoFill(operationType = OperationType.INSERT)
    void add(Setmeal setmeal);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select setmeal.*, setmeal_dish.id as setmeal_dish_id, setmeal_dish.price as setmeal_dish_price, setmeal_dish.name as setmeal_dish_name, setmeal_dish.setmeal_id, setmeal_dish.dish_id, setmeal_dish.copies from setmeal left outer join setmeal_dish on setmeal.id = setmeal_dish.setmeal_id where setmeal.id = #{id}")
    @ResultMap("setmealVOResultMap")
    SetmealVO select(Long id);

    @Update("update setmeal set status = #{status} where id = #{id}")
    void changeStatus(Integer status, Long id);

    @AutoFill(operationType = OperationType.UPDATE)
    void modify(Setmeal setmeal);

    void deleteById(List<Long> ids);

    @Select("select * from setmeal where category_id = #{categoryId}")
    List<Setmeal> list(long categoryId);

    @Select("select dish.description, dish.image, setmeal_dish.name, setmeal_dish.copies from setmeal_dish left outer join dish on setmeal_dish.dish_id = dish.id where setmeal_dish.setmeal_id = #{id}")
    List<DishItemVO> selectBySetmealId(Long id);
}
