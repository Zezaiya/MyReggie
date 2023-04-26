package com.zezai.dto;

import com.zezai.domain.Dish;
import com.zezai.domain.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*DTO:数据传输对象,一般用于展示层与服务层之间的数据传输*/

@Data      //其实就是扩展属性,因为返回给前端的类型可能不只是单单一个对象的各种属性,还包括其他的,所以通过DTO可以增强TM的属性
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
