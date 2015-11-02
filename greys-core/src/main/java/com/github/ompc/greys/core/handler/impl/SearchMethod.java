package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.GaMethod;
import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.handler.info.MethodInfo;
import com.github.ompc.greys.core.handler.matching.ClassMatching;
import com.github.ompc.greys.core.handler.matching.MethodMatching;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * 搜索已加载类的方法
 * Created by vlinux on 15/11/2.
 */
@Type("search-method")
public class SearchMethod extends MatchingSupport implements Handler<SearchMethod.Req,SearchMethod.Resp> {

    @Override
    public void init(int id, Session session) throws Throwable {

    }

    @Override
    public void destroy() {

    }

    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, Out<Resp> out) throws Throwable {

        final Collection<GaMethod> matchedGaMethods = matchingGaMethod(
                req.classMatching,
                req.isIncludeSubClasses,
                req.methodMatching
        );

        final Collection<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        for( GaMethod gaMethod : matchedGaMethods ) {
            methodInfos.add(new MethodInfo(gaMethod));
        }

        final Resp resp = new Resp();
        resp.methodInfos = methodInfos;
        out.finish(resp);

        return null;
    }

    public static class Req extends Handler.Req {

        // 类匹配
        private ClassMatching classMatching;

        // 是否包含子类
        private boolean isIncludeSubClasses;

        // 方法匹配
        private MethodMatching methodMatching;

    }

    public static class Resp extends Handler.Resp {

        private Collection<MethodInfo> methodInfos;

    }

}
