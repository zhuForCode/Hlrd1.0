package com.juhua.hangfen.eedsrd.model;

/**
 * Created by congj on 2017/10/17.
 */

public class JsonMessage<T> {
    private int code;
    private String message;
    private boolean success;
    private T Data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
