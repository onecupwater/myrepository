package com.douyiyuan.first.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private Integer code;  //状态码


    public ServiceException(Integer code,String msg){

        //super(msg); 这句代码的意思是，外界在new ServiceException并传进两个参数，code与msg时，将msg传给
        //父类RuntimeException，让后台打印报错时一并把msg打印出来，显示在日志上
        super(msg);

        this.code = code;
    }

}
