package com.zezai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zezai.domain.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
