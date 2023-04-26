package com.zezai.dto;

import com.zezai.domain.Setmeal;
import com.zezai.domain.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
