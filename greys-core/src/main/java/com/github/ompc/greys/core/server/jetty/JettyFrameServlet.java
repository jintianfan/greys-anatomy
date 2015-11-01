package com.github.ompc.greys.core.server.jetty;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * JettyFrameServlet
 * Created by vlinux on 15/10/24.
 */
public class JettyFrameServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(JettyHandlerSocket.class);
    }

}
