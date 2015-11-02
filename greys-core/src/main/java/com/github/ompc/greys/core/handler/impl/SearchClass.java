package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.handler.info.ClassInfo;
import com.github.ompc.greys.core.handler.matching.ClassMatching;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.Session;

import java.util.*;

/**
 * 搜索已加载类
 * Created by vlinux on 15/11/2.
 */
@Type("search-class")
public class SearchClass extends MatchingSupport implements Handler<SearchClass.Req, SearchClass.Resp> {

    @Override
    public void init(int id, Session session) throws Throwable {

    }

    @Override
    public void destroy() {

    }


    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, Out<Resp> out) throws Throwable {

        final Resp resp = new Resp();
        resp.classInfos = toClassInfos(matchingClasses(req.classMatching, req.isIncludeSubClasses), req);
        out.finish(resp);

        return null;
    }

    private Collection<ClassInfo> toClassInfos(final Collection<Class<?>> classSet, final Req req) {

        final Set<ClassInfo> classInfoSet = new LinkedHashSet<ClassInfo>();
        for (Class<?> clazz : classSet) {
            classInfoSet.add(new ClassInfo(
                    clazz,
                    req.isIncludeInterfaces,
                    req.isIncludeAnnotations,
                    req.isIncludeSuperClasses,
                    req.isIncludeClassLoaders,
                    req.isIncludeFields
            ));
        }

        return classInfoSet;
    }

    public static class Req extends Handler.Req {

        // 类匹配集合
        private ClassMatching classMatching;

        // 是否包含子类
        private boolean isIncludeSubClasses;

        // 是否包含属性
        private boolean isIncludeFields;

        // 是否包含ClassLoader
        private boolean isIncludeClassLoaders;

        // 是否包含接口
        private boolean isIncludeInterfaces;

        // 是否包含父类
        private boolean isIncludeSuperClasses;

        // 是否包含Annotation
        private boolean isIncludeAnnotations;

    }

    public static class Resp extends Handler.Resp {

        // 匹配类信息
        private Collection<ClassInfo> classInfos;

    }

}
