package com.github.ompc.greys.core.server;

import com.github.ompc.greys.core.Config;
import com.github.ompc.greys.core.server.jetty.JettyGreysServer;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * Greys服务器接口
 * Created by vlinux on 15/10/24.
 */
public interface Server {

    /**
     * 服务器绑定端口
     *
     * @param config 配置信息
     * @param inst   inst
     * @throws IOException 绑定失败
     */
    void bind(Config config, Instrumentation inst) throws IOException;

    /**
     * 服务器解除端口绑定
     *
     * @throws IOException 解除绑定失败
     */
    void unbind() throws IOException;

    /**
     * 服务器是否已绑定地址
     *
     * @return true:服务器已绑定地址/false:服务器未绑定地址
     */
    boolean isBind();

    class Factory {
        private final static Server instance = new JettyGreysServer();

        public static Server getInstance() {
            return instance;
        }
    }

}
