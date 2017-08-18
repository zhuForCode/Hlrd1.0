package com.juhua.hangfen.eedsrd.executor;

/**
 * Created by JiaJin Kuai on 2017/4/17.
 */

public interface IExecutor<T> {
    void execute();

    void onPrepare();

    void onSuccess(T t);

    void onFail(Exception e);
}
