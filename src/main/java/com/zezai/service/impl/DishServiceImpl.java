package com.zezai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.domain.Dish;
import com.zezai.domain.DishFlavor;
import com.zezai.dto.DishDto;
import com.zezai.mapper.DishMapper;
import com.zezai.service.DishFlavorService;
import com.zezai.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional   //涉及到多张表操作,需要开启事务
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品,同时插入菜品对应的口味数据,需要操作两张表,所以得自定义插入
    public void saveWithFlavor(DishDto dishDto) {
        //保存到菜表
        this.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> dishFlavors = dishDto.getFlavors();

        dishFlavors = dishFlavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存到菜品口味表
        dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {   //通过id查菜品信息和口味信息
        //查基本信息
       Dish dish= this.getById(id);

       DishDto dishDto=new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查询口味表
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {   //通过传来的对象修改口味表和菜品表
        //更新dish表
        this.updateById(dishDto);
        //清理当前菜品的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加提交过来的口味数据
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{      //为dish的id赋值
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
