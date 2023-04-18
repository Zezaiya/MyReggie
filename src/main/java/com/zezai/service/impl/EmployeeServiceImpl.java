package com.zezai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.domain.Employee;
import com.zezai.mapper.EmployeeMapper;
import com.zezai.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
