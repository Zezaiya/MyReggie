package com.zezai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zezai.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
