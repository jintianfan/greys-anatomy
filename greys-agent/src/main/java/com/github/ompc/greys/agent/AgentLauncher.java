package com.github.ompc.greys.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * 代理启动类
 * Created by vlinux on 15/5/19.
 */
public class AgentLauncher {

    // 全局持有ClassLoader用于隔离greys实现
    private static volatile ClassLoader greysClassLoader;

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }


    /**
     * 重置greys的classloader<br/>
     * 让下次再次启动时有机会重新加载
     */
    public synchronized static void resetGreysClassLoader() {
        greysClassLoader = null;
    }

    private static ClassLoader loadOrDefineClassLoader(final String coreJar) throws Throwable {

        final ClassLoader classLoader;

        // 如果已经被启动则返回之前启动的ClassLoader
        if (null != greysClassLoader) {
            classLoader = greysClassLoader;
        }

        // 如果未启动则重新加载
        else {
            classLoader = new URLClassLoader(new URL[]{new URL("file:" + coreJar)}) {

                @Override
                protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    final Class<?> loadedClass = findLoadedClass(name);
                    if (loadedClass != null) {
                        return loadedClass;
                    }

                    try {
                        Class<?> aClass = findClass(name);
                        if (resolve) {
                            resolveClass(aClass);
                        }
                        return aClass;
                    } catch (Exception e) {
                        return super.loadClass(name, resolve);
                    }
                }

            };

//            // 获取各种Hook
//            final Class<?> adviceWeaverClass = classLoader.loadClass("com.github.ompc.greys.core.advisor.AdviceWeaver");
//
//            // 初始化全局间谍
//            Spy.initForAgentLauncher(
//                    classLoader,
//                    adviceWeaverClass.getMethod("methodOnBegin",
//                            int.class,
//                            ClassLoader.class,
//                            String.class,
//                            String.class,
//                            String.class,
//                            Object.class,
//                            Object[].class),
//                    adviceWeaverClass.getMethod("methodOnReturnEnd",
//                            Object.class),
//                    adviceWeaverClass.getMethod("methodOnThrowingEnd",
//                            Throwable.class),
//                    adviceWeaverClass.getMethod("methodOnInvokeBeforeTracing",
//                            int.class,
//                            String.class,
//                            String.class,
//                            String.class),
//                    adviceWeaverClass.getMethod("methodOnInvokeAfterTracing",
//                            int.class,
//                            String.class,
//                            String.class,
//                            String.class),
//                    AgentLauncher.class.getMethod("resetGreysClassLoader")
//            );
        }

        return greysClassLoader = classLoader;
    }

    private static void injectSpyToBootstrapClassLoader(final Instrumentation inst) throws IOException {
        inst.appendToBootstrapClassLoaderSearch(
                new JarFile(AgentLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile())
        );
    }

    private static void initServer(final ClassLoader coreLoader, final String coreJson, final Instrumentation inst)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Config类定义
        final Class<?> classOfConfig = coreLoader.loadClass("com.github.ompc.greys.core.Config");

        // 反序列化成Config类实例
        final Object objectOfConfig = classOfConfig
                .getMethod("fromJson", String.class)
                .invoke(null, coreJson);

        // Server类定义
        final Class<?> classOfServer = coreLoader.loadClass("com.github.ompc.greys.core.server.Server");

        // 获取GaServer单例
        final Object objectOfServer = coreLoader.loadClass("com.github.ompc.greys.core.server.Server$Factory")
                .getMethod("getInstance")
                .invoke(null);

        // server.isBind()
        if (!(Boolean) classOfServer.getMethod("isBind").invoke(objectOfServer)) {
            // server.bind()
            classOfServer
                    .getMethod("bind", classOfConfig, Instrumentation.class)
                    .invoke(objectOfServer, objectOfConfig, inst);
        }

    }

    private static synchronized void main(final String args, final Instrumentation inst) {
        try {

            // 传递的args参数分两个部分:coreJar路径和coreArgs
            // 分别是greys-core的JAR包路径和期望传递到greys-core的参数
            final int index = args.indexOf(';');
            final String coreJar = args.substring(0, index);
            final String coreJson = args.substring(index+1, args.length());

            System.out.println(coreJson);

            // 将Spy添加到BootstrapClassLoader
            injectSpyToBootstrapClassLoader(inst);


            // 构造自定义的类加载器，尽量减少Greys对现有工程的侵蚀
            final ClassLoader coreLoader = loadOrDefineClassLoader(coreJar);

            // 启动server
            initServer(coreLoader, coreJson, inst);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

}
