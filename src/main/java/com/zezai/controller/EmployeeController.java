package com.zezai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zezai.common.Result;
import com.zezai.domain.Employee;
import com.zezai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")    //↓目的是将前端传来的数据存入session
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面输入的密码进行加密处理
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());   //加密

        //2.根据页面输入的用户名进行查询
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);   //通过mybatisPlus对service层进行操作

        //3.如果没有查询到则返回登录失败结果
        if(emp== null) {
            return Result.error("登录失败");
        }

        //4.密码比对,如果不一致则返回登录失败
        if(!emp.getPassword().equals(password)){
            return Result.error("登录失败");
        }

        //5.查看员工状态,如果已禁用则返回员工已被禁用结果
        if(emp.getStatus() == 0){
            return Result.error("账号已禁用");
        }

        //6.登录成功,将员工id存入Session并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }
}
