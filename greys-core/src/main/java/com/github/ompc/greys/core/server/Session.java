package com.github.ompc.greys.core.server;

/**
 * Greys会话上下文
 * Created by vlinux on 15/10/31.
 */
public interface Session {

    /**
     * 终结当前会话中的任意一个请求
     *
     * @param targetId 目标请求ID
     */
    void terminate(final int targetId);

    /**
     * 关闭会话
     */
    void closeSession();

}
