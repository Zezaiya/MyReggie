package com.zezai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.common.CustomException;
import com.zezai.domain.Setmeal;
import com.zezai.domain.SetmealDish;
import com.zezai.dto.SetmealDto;
import com.zezai.mapper.SetmealMapper;
import com.zezai.service.SetmealDishService;
import com.zezai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {   //新增套餐,并且添加套餐和菜品关系
        //保存套餐基本信息,操作setmeal,执行insert操作
        this.save(setmealDto);

        //保存套餐和菜品关系
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();   //获取到的对象里setmealid是空的,所以需要通过stream流赋值
        setmealDishes.stream().map((item) ->{      //为dish的id赋值
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithStatus(List<Long> ids) {   //根据传过来的id判断当前套餐是否是停售状态,同时删除关联菜品表里的数据

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId,ids);

        queryWrapper.eq(Setmeal::getStatus,1);

        int count=(int)this.count(queryWrapper);

        if(count>0){
           throw new CustomException("您无法删除启售中的商品");
        }

        this.removeByIds(ids);

        //删除SetmealDish关系表里的套餐信息
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();

        queryWrapper1.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper1);
    }


    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {  //数据回显,但是回显时要带上菜品信息
        Setmeal setmeal = this.getById(id);
        //创建SetmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        //进行对象的拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lqw);

        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) { //更新套餐表的同时更新套餐菜品关系表
        //1.更新套餐表
        this.updateById(setmealDto);

        //2.删除当前套餐的所有菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //3.添加修改后的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
