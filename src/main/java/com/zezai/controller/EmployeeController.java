package com.zezai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zezai.common.Result;
import com.zezai.domain.Employee;
import com.zezai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
/*员工管理*/
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //登录
    @PostMapping("/login")    //↓目的是将前端传来的数据存入session
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将页面输入的密码进行加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());   //加密

        //2.根据页面输入的用户名进行查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);   //通过mybatisPlus对service层进行操作

        //3.如果没有查询到则返回登录失败结果
        if (emp == null) {
            return Result.error("登录失败,账号或密码错误");
        }

        //4.密码比对,如果不一致则返回登录失败
        if (!emp.getPassword().equals(password)) {
            return Result.error("登录失败,账号或密码错误");
        }

        //5.查看员工状态,如果已禁用则返回员工已被禁用结果
        if (emp.getStatus() == 0) {
            return Result.error("登陆失败,账号已禁用");
        }

        //6.登录成功,将员工id存入Session并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }


    //退出
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }


    //添加员工
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //设置初始密码123456并进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置当前创建和更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());         通过公共字段自动填充可以省去这些代码,具体看MyMetaObjectHandler类

        //设置当前用户id
        Long empId = (Long) request.getSession().getAttribute("employee");  //先获取用户出入的Id
       // employee.setCreateUser(empId);
       // employee.setUpdateUser(empId);
        employeeService.save(employee);

        return Result.success("新增员工成功");
    }

    //员工信息分页查询
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {  //返回值类型里的泛型用的是mybatisPlus提供的Page类型,里面包含了与前端约定的records和total属性
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加条件查询
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);   //当name为空时不执行该操作

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);   //MybatisPlus会自动将我们添加的page里参数封装好

        return Result.success(pageInfo);

    }


    //更新(编辑,修改员工状态)
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return Result.success("员工信息更新成功");
    }

    //编辑时查询并回显数据(当编辑员工时页面会发送请求并携带上id,我们需要通过id进行查询并回显数据)
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null)
            return Result.success(employee);
        else
            return Result.error("未查询到对应员工信息");
    }

}
