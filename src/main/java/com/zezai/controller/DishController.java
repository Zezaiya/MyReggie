package com.zezai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zezai.common.CustomException;
import com.zezai.common.Result;
import com.zezai.domain.Category;
import com.zezai.domain.Dish;
import com.zezai.domain.DishFlavor;
import com.zezai.domain.SetmealDish;
import com.zezai.dto.DishDto;
import com.zezai.service.CategoryService;
import com.zezai.service.DishFlavorService;
import com.zezai.service.DishService;
import com.zezai.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/*菜品管理以及菜品口味*/
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService flavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    //分页查询
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {

        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.orderByDesc(Dish::getCategoryId);

        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);

        dishService.page(pageInfo, queryWrapper);   //MybatisPlus底层把queryWrapper得到的数据封装到pageInfo对象里

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Category category = categoryService.getById(item.getCategoryId());  //通过id查名字

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());   //这么多操作就是将Dish对象的属性拷贝给DishDto对象,然后再将特有的CategoryName属性赋给DishDto
        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    //新增菜品
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    //删除菜品
    @DeleteMapping
    public Result<String> delete(Long ids) {
        dishService.removeById(ids);
        return Result.success("删除成功");
    }

    //回显数据
    @GetMapping("/{id}")
    public Result<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if (dishDto != null) {
            return Result.success(dishDto);
        } else
            return Result.error("未查询到对应菜品信息");
    }

    //修改后保存数据
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return Result.success("修改菜品信息成功");
    }


    //修改菜品状态
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable("status") int status, @RequestParam("ids") List<Long> ids) {
        int count = getCount(ids);
        if (count > 0) {
            // 表示该菜品在其套餐中不能进行删除
            throw new CustomException("菜品在其套餐中使用无法进行停售");
        }
        ids.stream().forEach((item) -> {
            Dish dish = dishService.getById(item);
            dish.setStatus(status);
            dishService.updateById(dish);
        });

        return Result.success("修改成功");
    }

    /**
     * 查看菜品是否与其套餐关联
     *
     * @param ids
     * @return
     */
    private int getCount(List<Long> ids) {
        // 在停售前先查看是否在其套餐中使用
        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
        wrapper.in("dish_id", ids);

        int count = (int)setmealDishService.count(wrapper);
        return count;
    }

   /* @GetMapping("/list")
    public Result<List<Dish>> list(Dish dish){   //查询菜品
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus,1);//查询状态为1的套餐

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list= dishService.list(queryWrapper);

        return Result.success(list);
    }*/

    //获取菜品信息(包括口味)
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = flavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);
    }


}
