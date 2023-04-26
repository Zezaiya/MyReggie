package com.zezai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zezai.domain.User;

public interface UserService extends IService<User> {
    void sendMsg(String to,String subject,String context);
}
