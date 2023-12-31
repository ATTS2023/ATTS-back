package com.atts.attschedule.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//响应码 1成功 0 失败
    private String msg;//响应信息,描述字符串
    private Object data;//返回数据

    //增删改,成功响应
    public static Result success(){
        return new Result(1,"success",null);
    }
    //查,成功返回数据
    public static Result success(Object data){
        return new Result(1,"success",data);
    }
    //错误提示
    public static Result error(String msg){
        return new Result(0,msg,null);
    }
}

