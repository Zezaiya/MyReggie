package com.zezai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zezai.common.BaseContext;
import com.zezai.common.Result;
import com.zezai.domain.User;
import com.zezai.service.UserService;
import com.zezai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    //发送手机短信验证码
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {  //手机号不为空
            //随机生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调用阿里云提供的短信服务API完成发送短信
            // SMSUtils.sendMessage("zezai", "SMS_276372162", phone, code);

            log.info("验证码为:"+code);

            session.setAttribute("code", code);

            session.setAttribute("phone", phone);

            BaseContext.setCurrentId(user.getId());  //将用户id传入临时存储区,方便登录成功后取出id进行保存

            return Result.success("手机验证码短信发送成功");
        }

        return Result.error("验证码短信发送失败");
    }


    //登录
    @PostMapping("/login")     //因为传入的参数包括手机号和验证码,所以单独一个User无法接收,两种办法:1.userDto  2.用map接收
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        //参数中获取手机号
        String phone = map.get("phone").toString();

        //获取参数里的验证码
        String code = map.get("code").toString();

        //从Session中获取验证码
        Object curCode = session.getAttribute("code");

        //设置邮箱信息
        //String subject = "瑞吉餐购登录验证码";
        //String Yzm = ValidateCodeUtils.generateValidateCode(4).toString();
       // String context = "欢迎使用瑞吉餐购，登录验证码为: " + Yzm + ",五分钟内有效，请妥善保管!";

        // 真正地发送邮箱验证码
        //userService.sendMsg(phone, subject, context);

        //验证码比对
        if (curCode != null && curCode.equals(code)) {
            //比对成功则登陆成功,成功判断该手机号是否在能查到,查不到就自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();

            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);

            if(user==null){
                User Curuser=new User();
                Curuser.setId(BaseContext.getCurrentId());
                Curuser.setPhone(phone);
                Curuser.setStatus(1);  //开启状态
                userService.save(Curuser);
                user=Curuser;
            }

            session.setAttribute("user", user.getId()); //将user信息存入session

            return Result.success(user);
        }

        //比对失败则登录失败
        return Result.error("验证码错误,登陆失败");
    }
  @PostMapping("/logout")
    public Result<String> logout(HttpSession session){
        session.removeAttribute("code");
        session.removeAttribute("phone");
        return Result.success("注销成功");
  }

}
