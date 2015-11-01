package com.github.ompc.greys.core;

import com.google.gson.GsonBuilder;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;

/**
 * 配置信息
 * Created by vlinux on 15/10/24.
 */
public class Config {

    // 目标IP地址
    private String targetIp;

    // 目标端口号
    private int targetPort;

    // 目标进程ID
    private int targetProcessId;

    // GreysCoreJar包路径
    private String greysCoreJarPath;

    // GreysAgentJar包路径
    private String greysAgentJarPath;


    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public void setTargetProcessId(int targetProcessId) {
        this.targetProcessId = targetProcessId;
    }

    public void setGreysCoreJarPath(String greysCoreJarPath) {
        this.greysCoreJarPath = greysCoreJarPath;
    }

    public void setGreysAgentJarPath(String greysAgentJarPath) {
        this.greysAgentJarPath = greysAgentJarPath;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public int getTargetProcessId() {
        return targetProcessId;
    }

    public String getGreysCoreJarPath() {
        return greysCoreJarPath;
    }

    public String getGreysAgentJarPath() {
        return greysAgentJarPath;
    }


    /**
     * 序列化为Json
     *
     * @return config的json序列化字符串
     */
    public String toJson() {
        return new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_DASHES).create().toJson(this);
    }

    /**
     * 反序列化为对象
     *
     * @param json config的json序列化字符串
     * @return json对象
     */
    public static Config fromJson(final String json) {
        return new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_DASHES).create().fromJson(json, Config.class);
    }

}
