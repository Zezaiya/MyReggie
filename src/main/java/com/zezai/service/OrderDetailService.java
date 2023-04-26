package com.zezai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zezai.domain.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {

    List<OrderDetail> getOrderDetailsByOrderId(Long orderId);
}
