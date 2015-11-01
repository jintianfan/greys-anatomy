package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.handler.info.ClassInfo;
import com.github.ompc.greys.core.handler.matching.ClassMatching;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.server.Session;

import java.util.*;

/**
 * 搜索已加载类
 * Created by vlinux on 15/11/2.
 */
@Type("search-class")
public class SearchClass implements Handler<SearchClass.Req, SearchClass.Resp> {

    private final ReflectManager reflectManager = ReflectManager.Factory.getInstance();

    @Override
    public void init(int id, Session session) throws Throwable {

    }

    @Override
    public void destroy() {

    }


    @Override
    public LinkedHashMap<InvokeListener, ArrayList<PointCut>> handle(Req req, Out<Resp> out) throws Throwable {

        final Resp resp = new Resp();
        resp.classInfos = search(req.classesMatching).toArray(new ClassInfo[0]);
        out.finish(resp);

        return null;
    }

    private Set<ClassInfo> search(final ClassMatching[] classesMatching) {
        final Set<ClassInfo> classInfoSet = new LinkedHashSet<ClassInfo>();
        if (null == classesMatching) {
            return classInfoSet;
        }

        // 搜索所有匹配器需求
        for (final ClassMatching classMatching : classesMatching) {

            final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();

            // 搜索当前匹配器所匹配的类
            final Set<Class<?>> matchedClassSet = reflectManager.searchClass(classMatching.toMatcher());
            classSet.addAll(matchedClassSet);

            // 如果要求搜索子类，则需要继续添加
            if (classMatching.isIncludeSubClasses()) {
                for (final Class<?> matchedClass : matchedClassSet) {
                    classSet.addAll(reflectManager.searchSubClass(matchedClass));
                }
            }

            for (Class<?> matchedClass : classSet) {
                classInfoSet.add(new ClassInfo(
                        matchedClass,
                        classMatching.isIncludeInterfaces(),
                        classMatching.isIncludeAnnotations(),
                        classMatching.isIncludeSuperClasses(),
                        classMatching.isIncludeClassLoaders(),
                        classMatching.isIncludeFields()
                ));
            }

        }

        return classInfoSet;
    }

    private Set<Class<?>> searchClasses(final ClassMatching[] classesMatching) {
        final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        if (null == classesMatching) {
            return classSet;
        }

        // 搜索所有匹配器需求
        for (final ClassMatching classMatching : classesMatching) {

            // 搜索当前匹配器所匹配的类
            final Set<Class<?>> matchedClassSet = reflectManager.searchClass(classMatching.toMatcher());
            classSet.addAll(matchedClassSet);

            // 如果要求搜索子类，则需要继续添加
            if (classMatching.isIncludeSubClasses()) {
                for (final Class<?> matchedClass : matchedClassSet) {
                    classSet.addAll(reflectManager.searchSubClass(matchedClass));
                }
            }

        }

        return classSet;
    }

    public static class Req extends Handler.Req {

        // 类匹配集合
        private ClassMatching[] classesMatching;

    }

    public static class Resp extends Handler.Resp {

        // 匹配类信息
        private ClassInfo[] classInfos;

    }

}
