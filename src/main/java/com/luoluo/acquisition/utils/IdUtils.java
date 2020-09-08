package com.luoluo.acquisition.utils;

import cn.hutool.core.lang.Snowflake;

import java.util.Random;

/**
 * @ClassName IdUtils
 * @Description TODO
 * @Author 洛洛
 * @Date 2020/9/8 22:50
 * @Version V1.0
 **/
public class IdUtils {
    public static   Long getId(){
        return  new Snowflake(getRandomDigital(),getRandomDigital()).nextId();
    }

    public static Integer getRandomDigital(){
       return new Random().nextInt(20);
    }
}
