package com.zezai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zezai.domain.Orders;


public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
