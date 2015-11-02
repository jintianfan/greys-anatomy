package com.github.ompc.greys.core;

import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.handler.Type;
import com.github.ompc.greys.core.handler.impl.SearchClass;
import com.github.ompc.greys.core.util.GaClassUtils;

import java.util.Set;

/**
 * Created by vlinux on 15/10/29.
 */
public class Test {

    public static void main(String... args) throws Throwable {

        System.out.println(Handler.class.isAssignableFrom(SearchClass.class));

        final Set<Class<?>> classSet = GaClassUtils.scanPackage(Test.class.getClassLoader(), "com.github.ompc.greys.core.handler.impl");
        System.out.println(GaClassUtils.filterByAnnotation(classSet, Type.class));
        System.out.println(GaClassUtils.filterByParentClass(classSet, Handler.class));


    }

}
