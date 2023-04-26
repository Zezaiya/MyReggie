package com.zezai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.domain.DishFlavor;
import com.zezai.mapper.DishFlavorMapper;
import com.zezai.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
