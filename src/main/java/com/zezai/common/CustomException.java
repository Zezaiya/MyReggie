package com.zezai.common;
/*处理客户端的各种异常*/
public class CustomException extends RuntimeException{
    public CustomException(String message){
           super(message);
    }
}
