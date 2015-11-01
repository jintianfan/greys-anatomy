package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;
import com.github.ompc.greys.core.util.LogUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 终结请求
 * Created by vlinux on 15/10/31.
 */
@Type("terminate")
public class Terminate implements Handler<Terminate.Req, Handler.Resp> {

    private final Logger logger = LogUtil.getLogger();
    private Session session;

    @Override
    public void init(int id, Session session) throws Throwable {
        this.session = session;
    }

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, Out<Resp> out) throws Throwable {

        final int targetId = req.targetId;
        session.terminate(targetId);
        out.finish();

        logger.info("terminate targetId={}", targetId);

        return null;
    }

    @Override
    public void destroy() {

    }

    public static class Req extends Handler.Req {
        private int targetId;
    }

}
