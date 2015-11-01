package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.github.ompc.greys.core.util.GaStringUtils.version;

/**
 * 获取版本号
 * Created by vlinux on 15/10/25.
 */
@Type("get-version")
public class GetVersion implements Handler<Handler.Req, GetVersion.Resp> {

    @Override
    public void init(int id, Session session) throws Throwable {

    }

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, Out<Resp> out) throws Throwable {
        final Resp resp = new Resp();
        resp.version = version();
        out.finish(resp);
        return null;
    }

    @Override
    public void destroy() {

    }

    public static class Resp extends Handler.Resp {
        private String version;
    }

}
