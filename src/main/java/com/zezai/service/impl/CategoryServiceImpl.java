package com.zezai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.common.CustomException;
import com.zezai.domain.Category;
import com.zezai.domain.Dish;
import com.zezai.domain.Setmeal;
import com.zezai.mapper.CategoryMapper;
import com.zezai.service.CategoryService;
import com.zezai.service.DishService;
import com.zezai.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setMealService;


    @Override
    public void remove(Long id) {
        //添加查询条件,根据分类id进行查询
        //查询当前分类是否关联其他菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        Long count1= dishService.count(dishLambdaQueryWrapper);

        if(count1>0){
             throw new CustomException("当前分类项关联到其他菜品,删除失败");                 /*已经关联到其他菜品.抛出异常*/
        }

        //查询当前分类是否关联其他套餐
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper=new LambdaQueryWrapper<>();

        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        Long count2=setMealService.count(setMealLambdaQueryWrapper);

        if (count2>0){
            throw new CustomException("当前分类项关联到其他套餐,删除失败");                   /* 已经关联到其他套餐,抛出异常*/
        }
        //正常删除
        super.removeById(id);
    }



}
