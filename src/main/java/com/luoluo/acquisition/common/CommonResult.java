package com.luoluo.acquisition.common;

/**
 * @program: lx
 * @description: 封装接口返回类
 * @author: luoluo
 * @create: 2020-03-27 14:14
 * @modified By:
 */
public class CommonResult<T> {
    private static final String CODE_SUCCESS = "200";

    private static final String CODE_FAIL = "400";

    private static final String MSG_SUCCESS="success";

    private static final String MSG_FAIL="failed";

    public CommonResult(){
    }
    public CommonResult(String code ){
        this.code=code;
    }
    public CommonResult(String code, T entity ){
        this.code=code;
        this.entity=entity;
    }
    public CommonResult(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public CommonResult(String code, String msg, T entity) {
        this.code = code;
        this.msg = msg;
        this.entity=entity;
    }
    public static CommonResult  success(){
        return new CommonResult (CODE_SUCCESS,MSG_SUCCESS);
    }

    public static CommonResult  success(Object data){
        return new CommonResult (CODE_SUCCESS,MSG_SUCCESS, data);
    }

    public static CommonResult  fail(){
        return new CommonResult (CODE_FAIL, MSG_FAIL);
    }

    private String code;

    private String msg;

    public T entity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
