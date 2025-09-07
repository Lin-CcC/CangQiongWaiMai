package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    // select * from category where name = xx and type = xx
    Page<Category> categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(operationType = OperationType.UPDATE)
    void modifyCategory(Category category);

    @AutoFill(operationType = OperationType.UPDATE)
    @Update("update category set " + " status = #{status} ," + "update_time = #{updateTime}," + "update_user = #{updateUser}" + " where id = #{id}")
    void changeStatus(Category category);

    @AutoFill(operationType = OperationType.INSERT)
    @Insert("insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES (#{id}, #{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addCategory(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    List<Category> searchCategory(Category category);
}
