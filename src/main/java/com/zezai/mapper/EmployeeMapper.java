package com.zezai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zezai.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
