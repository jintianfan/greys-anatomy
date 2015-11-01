package com.github.ompc.greys.core.util;

/**
 * 反射工具类
 * Created by vlinux on 15/11/2.
 */
public class GaReflectUtils {

    /*
     * 计算ClassType
     */
    public static int computeClassType(Class<?> targetClass) {
        int type = 0;
        if (targetClass.isAnnotation())
            type |= TYPE_ANNOTATION;
        if (targetClass.isAnonymousClass())
            type |= TYPE_ANONYMOUS;
        if (targetClass.isArray())
            type |= TYPE_ARRAY;
        if (targetClass.isEnum())
            type |= TYPE_ENUM;
        if (targetClass.isInterface())
            type |= TYPE_INTERFACE;
        if (targetClass.isLocalClass())
            type |= TYPE_LOCAL;
        if (targetClass.isMemberClass())
            type |= TYPE_MEMBER;
        if (targetClass.isPrimitive())
            type |= TYPE_PRIMITIVE;
        if (targetClass.isSynthetic())
            type |= TYPE_SYNTHETIC;
        return type;
    }


    public static final int TYPE_ANNOTATION = 1 << 0;
    public static final int TYPE_ANONYMOUS = 1 << 1;
    public static final int TYPE_ARRAY = 1 << 2;
    public static final int TYPE_ENUM = 1 << 3;
    public static final int TYPE_INTERFACE = 1 << 4;
    public static final int TYPE_LOCAL = 1 << 5;
    public static final int TYPE_MEMBER = 1 << 6;
    public static final int TYPE_PRIMITIVE = 1 << 7;
    public static final int TYPE_SYNTHETIC = 1 << 8;

    /**
     * 默认类型(全匹配)
     */
    public static final int DEFAULT_TYPE =
            TYPE_ANNOTATION
                    | TYPE_ANONYMOUS | TYPE_ARRAY | TYPE_ENUM
                    | TYPE_INTERFACE | TYPE_LOCAL | TYPE_MEMBER
                    | TYPE_PRIMITIVE | TYPE_SYNTHETIC;


}
