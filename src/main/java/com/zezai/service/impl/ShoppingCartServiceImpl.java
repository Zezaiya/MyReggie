package com.zezai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.domain.ShoppingCart;
import com.zezai.mapper.ShoppingCartMapper;
import com.zezai.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
