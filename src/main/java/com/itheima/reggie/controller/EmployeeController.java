package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//        处理逻辑如下：
//        1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }
//        4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
//        5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("登录失败,账户禁用");
        }
//        6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
//        清理session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

//    在开发代码之前，需要梳理一下整个程序的执行过程：
//1、页面发送ajax请求，将新增员工页面中输入的数据以json的形式提交到服务端
//2、服务端Controller接收页面提交的数据并调用Service将数据进行保存
//3、Service调用Mapper操作数据库，保存数据



    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
            log.info("新增员工，员工信息{}",employee.toString());
            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());

      Long empId =(Long) request.getSession().getAttribute("employee");

             employee.setCreateUser(empId);
             employee.setUpdateUser(empId);

             employeeService.save(employee);

                return R.success("新增成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page= {},pageSize= {},name= {}",page,pageSize,name);

//        分页构造
       Page pageInfo = new Page(page,pageSize);
//        条件构造
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        Long empId= (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("更新成功");
    }

    @GetMapping("{id}")
        public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }



}
