package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.result.Result;

import java.util.List;
import java.util.Random;

public interface AddressBookService {
    Result add(AddressBook addressBook);

    List<AddressBook> list();

    AddressBook selectDefaultAddress();

    void modifyById(AddressBook addressBook);

    void deleteById(Long id);

    AddressBook selectById(Long id);

    void setDefaultAddress(AddressBook addressBook);
}
