package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;
import com.google.gson.annotations.SerializedName;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by vlinux on 15/10/31.
 */
@Type("echo")
public class Echo implements Handler<Echo.Req, Echo.Resp> {

    private volatile boolean isRunning;

    @Override
    public void init(int id, Session session) throws Throwable {
        isRunning = true;
    }

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, final Out<Resp> out) throws Throwable {
        final Resp resp = new Resp();
        resp.words = req.words;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning) {
                        out.out(resp);
                        Thread.sleep(1000L);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


        return null;
    }

    @Override
    public void destroy() {
        isRunning = false;
    }

    public static class Req extends Handler.Req {

        @NotBlank
        @SerializedName("words")
        private String words;

    }

    public static class Resp extends Handler.Resp {

        @NotBlank
        @SerializedName("words")
        private String words;

    }

}
