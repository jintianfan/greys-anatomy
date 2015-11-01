package com.github.ompc.greys.core.server;

/**
 * 帧格式错误
 * Created by vlinux on 15/10/29.
 */
public class FrameFormatException extends Exception {

    public FrameFormatException(Throwable cause) {
        super("illegal frame format", cause);
    }
}
