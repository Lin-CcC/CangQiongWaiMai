package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface AddressBookMapper {

    @Insert("insert into address_book (id, user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "VALUES" +
            " (#{id}, #{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void add(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> list(Long userId);

    @Select("select * from address_book where user_id = user_id and is_default = 1")
    AddressBook selectDefaultAddress(Long userId);

    void modifyById(AddressBook addressBook);

    @Delete("delete from address_book where user_id = #{userId} and id = #{id}")
    void deleteById(Long id, Long userId);

    @Select("select * from address_book where id = #{id} and user_id = #{userId}")
    AddressBook selectById(Long id, Long userId);

    @Update("update address_book set is_default = 1 where user_id = #{userId} and id = #{id}")
    void setDefaultAddress(AddressBook addressBook);

    @Update("update address_book set is_default = 0 where user_id = #{userId} and id = #{id}")
    void updateDefaultToNot(AddressBook addressBook);
}
