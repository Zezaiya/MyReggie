package com.zezai.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zezai.domain.OrderDetail;
import com.zezai.mapper.OrderDetailMapper;
import com.zezai.service.OrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    /**
     * 通过id获取用户的订单详情
     * @param orderId
     * @return
     */
    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = this.list(queryWrapper);
        return orderDetailList;
    }
}