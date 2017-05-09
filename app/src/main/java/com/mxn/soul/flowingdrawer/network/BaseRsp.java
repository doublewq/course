package com.mxn.soul.flowingdrawer.network;

/**
 * Created by weijie.liu on 16/04/2017.
 */

public class BaseRsp<T> {
    int code;
    String errMsg;
    T data;
}
