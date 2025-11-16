package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    AddressBookService addressBookService;

    @PostMapping()
    public Result add(@RequestBody AddressBook addressBook){
        addressBookService.add(addressBook);
        log.info("调用了add");
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> list(){
        List<AddressBook> list = addressBookService.list();
        log.info("调用了list");
        return Result.success(list);
    }

    @GetMapping("/default")
    public Result<AddressBook> selectDefaultAddress(){
        AddressBook addressBook = addressBookService.selectDefaultAddress();
        log.info("调用了selectDefaultAddress");
        return Result.success(addressBook);
    }

    @PutMapping()
    public Result modifyById(@RequestBody AddressBook addressBook){
        addressBookService.modifyById(addressBook);
        log.info("调用了modifyById");
        return Result.success();
    }

    @DeleteMapping()
    public Result deleteById(Long id){
        addressBookService.deleteById(id);
        log.info("调用了deleteById");
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressBook> selectById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.selectById(id);
        log.info("调用了selectById");
        return Result.success(addressBook);
    }

    @PutMapping("/default")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        addressBookService.setDefaultAddress(addressBook);
        log.info("调用了setDefaultAddress");
        return Result.success();
    }
}
