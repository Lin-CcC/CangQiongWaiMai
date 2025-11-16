package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sky.constant.DefaultAddressConstant.ISDEFAULT;
import static com.sky.constant.DefaultAddressConstant.NOTDEFAULT;

@Slf4j
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> list() {
        long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookMapper.list(userId);
        log.info("当前登录用户的地址为：{}", list);
        return list;
    }

    @Override
    public Result add(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        List<AddressBook> list = addressBookMapper.list(userId);
        log.info("现在该用户的地址为：{}", list);
        //如果这是用户的第一个地址，就默认设置为默认地址,如果这不是用户的第一个地址，就直接插入.
        if (list.isEmpty()){
            //设置默认地址
            addressBook.setIsDefault(ISDEFAULT);
        }else{
            //设置非默认地址
            addressBook.setIsDefault(NOTDEFAULT);
        }
        log.info("当前用户新增的地址为：{}", addressBook);
        addressBookMapper.add(addressBook);
        return null;
    }

    @Override
    public AddressBook selectDefaultAddress() {
        long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.selectDefaultAddress(userId);
        log.info("当前用户的默认地址为：{}", addressBook);
        return addressBook;
    }

    @Override
    public void modifyById(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookMapper.modifyById(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        Long userId = BaseContext.getCurrentId();
        addressBookMapper.deleteById(id, userId);
    }

    @Override
    public AddressBook selectById(Long id) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.selectById(id, userId);
        return addressBook;
    }

    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        AddressBook currentDefaultAddress = addressBookMapper.selectDefaultAddress(userId);
        if (currentDefaultAddress != null){
            //如果之前有默认地址，则更改之前默认地址为非默认
            addressBookMapper.updateDefaultToNot(currentDefaultAddress);
        }
        addressBookMapper.setDefaultAddress(addressBook);
    }
}
