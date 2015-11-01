package com.github.ompc.greys.core.server.jetty;

import com.github.ompc.greys.core.Config;
import com.github.ompc.greys.core.manager.HandlerMetaDataManager;
import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.server.Server;
import com.github.ompc.greys.core.util.LogUtil;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

/**
 * JettyWebSocketServer
 * Created by vlinux on 15/10/24.
 */
public class JettyGreysServer implements Server {

    /**
     * 访问路径前缀
     */
    private static final String PATH_PREFIX = "/greys/";

    private final Logger logger = LogUtil.getLogger();
    private final AtomicBoolean isStartupRef = new AtomicBoolean(false);

    private Config config;
    private Instrumentation inst;

    private InetSocketAddress address;
    private org.eclipse.jetty.server.Server server;

    /**
     * 初始化各种资源
     * 我没有找到一个更好的DI框架整合jetty，所以这里只好用非常矬的方式做单例模式。
     * 你们如果能找到一个更好的，欢迎推荐
     *
     * @param inst
     */
    private void init(final Instrumentation inst) {
        HandlerMetaDataManager.Factory.getInstance();
        ReflectManager.Factory.initInstance(inst);
    }

    @Override
    public void bind(final Config config, final Instrumentation inst) throws IOException {
        if (!isStartupRef.compareAndSet(false, true)) {
            throw new IllegalStateException();
        }

        this.config = config;
        this.inst = inst;

        // init resource
        init(inst);

        // init server
        this.address = new InetSocketAddress(config.getTargetIp(), config.getTargetPort());
        server = newJettyServer();

        // start server
        try {
            server.start();
            logger.info("{} bind success. address={}", this, address);
        } catch (Throwable t) {
            logger.debug("{} bind failed. address={}", this, address, t);
            throw new IOException(t);
        }

    }

    /*
     * 构造JettyServer
     */
    private org.eclipse.jetty.server.Server newJettyServer() {
        final ServletContextHandler context = new ServletContextHandler(null, "/", SESSIONS);
        context.addServlet(new ServletHolder("greys-ws-frame-servlet", JettyFrameServlet.class), PATH_PREFIX);
        context.setClassLoader(JettyGreysServer.class.getClassLoader());
        final org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(address);
        server.setHandler(context);
        server.setStopAtShutdown(true);
        return server;
    }

    @Override
    public void unbind() throws IOException {
        if (!isStartupRef.compareAndSet(true, false)) {
            throw new IllegalStateException();
        }

        try {
            server.stop();
            logger.info("{} unbind success.", this);
        } catch (Throwable t) {
            logger.debug("{} unbind failed. address={}", this, address);
            throw new IOException(t);
        } finally {
            server.destroy();
        }


    }

    @Override
    public boolean isBind() {
        return isStartupRef.get();
    }

    @Override
    public String toString() {
        return "greys-websocket-jetty-server";
    }
}
