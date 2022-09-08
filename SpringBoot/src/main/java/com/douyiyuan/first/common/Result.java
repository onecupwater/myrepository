package com.douyiyuan.first.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Integer code; //状态码

    private String msg; //信息

    private Object data; //数据

    public static Result success (Object data){
        return new Result(StatusCode.CODE_200,null,data);
    }


    public static Result success (String msg,Object data){
        return new Result(StatusCode.CODE_200,msg,data);
    }

    public static Result error (Integer code,String msg){
        return new Result(code,msg,null);
    }
}
