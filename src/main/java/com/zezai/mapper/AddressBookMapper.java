package com.zezai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zezai.domain.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
