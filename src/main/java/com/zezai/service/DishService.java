package com.zezai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zezai.domain.Dish;
import com.zezai.dto.DishDto;


public interface DishService extends IService<Dish>{
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
