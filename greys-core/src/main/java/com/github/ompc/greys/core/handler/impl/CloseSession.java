package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 关闭会话
 * Created by vlinux on 15/10/31.
 */
@Type("close-session")
public class CloseSession implements Handler<Handler.Req, Handler.Resp> {

    private Session session;

    @Override
    public void init(int id, Session session) throws Throwable {
        this.session = session;
    }

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, Out<Resp> out) throws Throwable {
        session.closeSession();
        out.finish();
        return null;
    }

    @Override
    public void destroy() {

    }
}
