package com.github.ompc.greys.core;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.List;

import static com.github.ompc.greys.core.util.GaStringUtils.getCauseMessage;

/**
 * 核心启动器
 * Created by vlinux on 15/11/1.
 */
public class CoreLauncher {

    public CoreLauncher(String[] args) throws Exception {

        // 解析配置文件
        final Config config = analyzeConfig(args);

        // 加载agent
        attachAgent(config);

    }

    /*
     * 解析Configure
     */
    private Config analyzeConfig(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.accepts("pid").withRequiredArg().ofType(int.class).required();
        parser.accepts("target").withOptionalArg().ofType(String.class);
        parser.accepts("core").withOptionalArg().ofType(String.class);
        parser.accepts("agent").withOptionalArg().ofType(String.class);

        final OptionSet os = parser.parse(args);
        final Config config = new Config();

        if (os.has("target")) {
            final String[] strSplit = ((String) os.valueOf("target")).split(":");
            config.setTargetIp(strSplit[0]);
            config.setTargetPort(Integer.valueOf(strSplit[1]));
        }

        config.setTargetProcessId((Integer) os.valueOf("pid"));
        config.setGreysAgentJarPath((String) os.valueOf("agent"));
        config.setGreysCoreJarPath((String) os.valueOf("core"));

        return config;
    }

    /*
     * 加载Agent
     */
    private void attachAgent(Config config) throws Exception {

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> vmdClass = loader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
        final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");
        final int targetProcessId = config.getTargetProcessId();

        Object attachVmdObj = null;
        for (Object obj : (List<?>) vmClass.getMethod("list", (Class<?>[]) null).invoke(null, (Object[]) null)) {
            if ((vmdClass.getMethod("id", (Class<?>[]) null).invoke(obj, (Object[]) null))
                    .equals(Integer.toString(targetProcessId))) {
                attachVmdObj = obj;
            }
        }

//        if (null == attachVmdObj) {
//            // throw new IllegalArgumentException("pid:" + configure.getJavaPid() + " not existed.");
//        }

        Object vmObj = null;
        try {
            if (null == attachVmdObj) { // 使用 attach(String pid) 这种方式
                vmObj = vmClass.getMethod("attach", String.class).invoke(null, "" + targetProcessId);
            } else {
                vmObj = vmClass.getMethod("attach", vmdClass).invoke(null, attachVmdObj);
            }
            vmClass.getMethod("loadAgent", String.class, String.class)
                    .invoke(vmObj, config.getGreysAgentJarPath(), config.getGreysCoreJarPath() + ";" + config.toJson());
        } finally {
            if (null != vmObj) {
                vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
            }
        }

    }


    public static void main(String[] args) {
        try {
            new CoreLauncher(args);
        } catch (Throwable t) {
            System.err.println("start greys failed, because : " + getCauseMessage(t));
            System.exit(-1);
        }
    }

}
