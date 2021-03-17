package com.pei.httpmanager;

import java.lang.reflect.Type;

public abstract class ResponseCallback<T> {

    /**
     * 对应泛型T的类型
     * （受限于java的假泛型，否则就不需要这个参数了，可以直接T.class）
     */
    Type type;

    public ResponseCallback(Type type) {
        this.type = type;
    }

    public abstract void onSuccess(T data);

    public abstract void onError(Exception e);
}