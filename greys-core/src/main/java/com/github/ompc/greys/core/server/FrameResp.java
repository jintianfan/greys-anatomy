package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.handler.Handler;
import com.google.gson.annotations.SerializedName;

/**
 * 应答帧
 * Created by vlinux on 15/10/24.
 */
public class FrameResp {

    /**
     * 应答状态：正常
     */
    public static final int STATUS_SUCCESS = 200;

    /**
     * 应答状态：未知错误
     */
    public static final int STATUS_UN_KNOW_ERROR = 500;

    /**
     * 应答状态：非法帧类型
     */
    public static final int STATUS_ILLEGAL_FRAME_TYPE = 502;

    /**
     * 应答状态：非法数据格式
     */
    public static final int STATUS_ILLEGAL_BODY_FORMAT = 503;

    /**
     * 应答状态：处理失败
     */
    public static final int STATUS_HANDLER_ERROR = 504;

    /**
     * 应答状态：非法的参数
     */
    public static final int STATUS_ILLEGAL_PARAMETERS = 505;

    /**
     * 应答状态：请求已在处理中
     */
    public static final int STATUS_HANDLING = 506;

    // 帧序号
    @SerializedName("id")
    private final int id;

    // 应答状态
    @SerializedName("status")
    private final int status;

    // 是否完结
    @SerializedName("is-finish")
    private final boolean isFinish;

    // 应答体
    @SerializedName("body")
    private Handler.Resp body;

    public FrameResp(int id, int status, boolean isFinish) {
        this.id = id;
        this.status = status;
        this.isFinish = isFinish;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public Handler.Resp getBody() {
        return body;
    }

    public void setBody(Handler.Resp body) {
        this.body = body;
    }

    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public String toString() {
        return String.format("[id=%s;status=%s;finish=%s;]", id, status, isFinish);
    }
}
