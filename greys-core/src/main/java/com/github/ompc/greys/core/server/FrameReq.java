package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.handler.Handler;

/**
 * 请求帧
 * Created by vlinux on 15/10/24.
 */
public class FrameReq {

    // 帧序号
    private int id;

    // 请求类型
    private String type;

    // 处理数据
    transient private Handler.Req body;

    // 处理类型
    transient private Class<? extends Handler> handlerClass;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Handler.Req getBody() {
        return body;
    }

    public void setBody(Handler.Req body) {
        this.body = body;
    }

    public Class<? extends Handler> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<? extends Handler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    @Override
    public String toString() {
        return String.format("[id=%s;type=%s;]", id, type);
    }
}
