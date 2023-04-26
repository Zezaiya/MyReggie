package com.zezai.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/*
公共字段比如更新时间不需要重复写,所以我们可以通过自动填充的方式实现
方法:1.在实体类需要被自动填充的属性上添加@TableField注解,并指定自动填充策略
    2.按照MybatisPlus要求编写自定义元数据对象处理器,为公共对象赋值(实现MetaObjectHandler接口)

自定义元数据处理器,用于填充公共字段
*/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    private HttpSession httpSession;     //通过Spring自动注入的方式获取Session对象从而得到用户id
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", (Long)httpSession.getAttribute("employee"));
        metaObject.setValue("updateUser", (Long)httpSession.getAttribute("employee"));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", (Long)httpSession.getAttribute("employee"));
    }
}
