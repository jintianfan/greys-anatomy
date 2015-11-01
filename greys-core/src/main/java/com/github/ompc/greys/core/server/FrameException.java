package com.github.ompc.greys.core.server;

/**
 * 帧异常
 * Created by vlinux on 15/10/25.
 */
public class FrameException extends Exception {

    private final int status;

    public FrameException(int status, Throwable cause) {
        super("state=" + status, cause);
        this.status = status;
    }

    public FrameException(int status) {
        super("state=" + status);
        this.status = status;
    }

    /**
     * 获取错误状态码
     *
     * @return 错误状态码
     */
    public int getStatus() {
        return status;
    }
}
