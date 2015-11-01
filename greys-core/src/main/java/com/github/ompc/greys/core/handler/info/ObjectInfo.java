package com.github.ompc.greys.core.handler.info;

/**
 * 对象信息
 * Created by vlinux on 15/11/2.
 */
public class ObjectInfo {

    private final String valueOfString;

    public ObjectInfo(final Object object) {
        if (null == object) {
            valueOfString = "null";
        } else {
            valueOfString = object.toString();
        }
    }

}
