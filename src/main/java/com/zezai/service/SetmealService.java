package com.zezai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zezai.domain.Setmeal;
import com.zezai.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void deleteWithStatus(List<Long> ids);

    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}
