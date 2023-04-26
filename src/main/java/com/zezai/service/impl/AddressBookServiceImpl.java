package com.zezai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.domain.AddressBook;
import com.zezai.mapper.AddressBookMapper;
import com.zezai.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
