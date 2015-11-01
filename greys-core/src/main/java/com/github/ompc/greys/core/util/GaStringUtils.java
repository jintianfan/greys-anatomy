package com.github.ompc.greys.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Greys内部的字符串工具类
 * Created by vlinux on 15/10/24.
 */
public class GaStringUtils {

    /**
     * 翻译类名称<br/>
     * 将 java/lang/String 的名称翻译成 java.lang.String
     *
     * @param className 类名称 java/lang/String
     * @return 翻译后名称 java.lang.String
     */
    public static String tranClassName(String className) {
        return StringUtils.replace(className, "/", ".");
    }

    /**
     * 返回Greys当前版本
     *
     * @return Greys当前版本
     * @throws IOException 获取版本号出错(一般不会发生)
     */
    public static String version() throws IOException {
        final InputStream is = GaStringUtils.class.getResourceAsStream("/com/github/ompc/greys/core/res/version");
        try {
            return IOUtils.toString(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 获取异常的原因描述
     *
     * @param t 异常
     * @return 异常原因
     */
    public static String getCauseMessage(Throwable t) {
        if (null != t.getCause()) {
            return getCauseMessage(t.getCause());
        }
        return t.getMessage();
    }

}
