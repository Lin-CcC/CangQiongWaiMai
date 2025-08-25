package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    // select * from category where name = xx and type = xx
    Page<Category> categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void modifyCategory(CategoryDTO categoryDTO);

    @Update("update category set status = #{status} where id = #{id}")
    void changeStatus(int status, int id);

    @Insert("insert into category (id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES (#{id}, #{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void addCategory(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteById(long id);

    List<Category> searchCategory(Category category);
}
