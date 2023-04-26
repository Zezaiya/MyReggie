package com.zezai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zezai.common.Result;
import com.zezai.domain.Category;
import com.zezai.domain.Setmeal;
import com.zezai.dto.SetmealDto;
import com.zezai.service.CategoryService;
import com.zezai.service.SetmealDishService;
import com.zezai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;


    @Autowired
    private CategoryService categoryService;

    //分页查询
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        //因为前端需要我们返回的包括套餐的分类,而setmeal并没有这个属性,setmealDto有,所以我们有以下解决方法
        //想DishController那样,new一个SetmealDto对象,将setmeal的值拷贝到Dto里,并再为Dto里的套餐分类categoryId赋值

        Page<Setmeal> pageInfo = new Page(page, pageSize);

        Page<SetmealDto> dtoPage = new Page();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(name != null, Setmeal::getName, name);//通过id模糊查询

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");//records不需要拷贝,因为泛型不一致

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return Result.success(dtoPage);
    }


    //添加套餐
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Result.success("套餐添加成功");
    }

    //删除套餐
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteWithStatus(ids);
        return Result.success("删除成功");
    }

    //回显数据
    @GetMapping("/{id}")
    public Result<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto=setmealService.getByIdWithDish(id);
        return Result.success(setmealDto);
    }

    //修改状态
    @PostMapping("/status/{status}")
    public Result<String> changeStatus(@PathVariable int status,@RequestParam Long ids) {
        Setmeal setmeal = setmealService.getById(ids);
        if (setmeal.getStatus() == 1) {
            setmeal.setStatus(0);
        } else {
            setmeal.setStatus(1);
        }
           setmealService.updateById(setmeal);
           return Result.success("修改套餐状态成功");
    }

    //修改后保存
    @PutMapping
    private Result<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return Result.success("套餐修改成功");
    }


    //通过id查询套餐信息
    @GetMapping("/list")
    private Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId() !=null,Setmeal::getCategoryId,setmeal.getCategoryId());

        queryWrapper.eq(setmeal.getStatus() !=null,Setmeal::getStatus,setmeal.getStatus());

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList=setmealService.list(queryWrapper);

        return Result.success(setmealList);
    }


}
