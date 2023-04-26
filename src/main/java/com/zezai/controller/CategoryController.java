package com.zezai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zezai.common.Result;
import com.zezai.domain.Category;
import com.zezai.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*分类管理*/
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    //新增菜品or套餐分类
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        categoryService.save(category);
        return Result.success("新增分类成功");
    }


    //分页查询
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        Page pageInfo=new Page(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);

    }

    //删除分类
    @DeleteMapping
    public Result<String> delete(Long ids){    //因为是类似于get方式获取的id,所以不需要@RequestBody
        // MybatisPlus为我们提供的删除操作无法对套餐之间的关系进行判断,如果套餐包含菜品是无法直接删除的,所以需要我们自定义删除操作
           categoryService.remove(ids);
        return Result.success("删除成功");
    }

    //更新
    @PutMapping
    public Result<String> update( @RequestBody Category category){
       categoryService.updateById(category);
       return Result.success("修改信息成功");
    }

    //获取菜品管理-添加菜品-菜品分类下滑框内容
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
       LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();

       queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

       queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);  //优先分类排序,后是更新时间排序

       List<Category> list= categoryService.list(queryWrapper);

       return Result.success(list);
    }
}
