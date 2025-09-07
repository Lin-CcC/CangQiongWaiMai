package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface EmployeeService {

    void editEmployee(EmployeeDTO employeeDTO);

    Employee selectEmployeeById(Long id);

    void setStatus(Integer status, Long id);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    PageResult employeePageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    Result<String> saveEmployee(EmployeeDTO employeeDTO);


    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
