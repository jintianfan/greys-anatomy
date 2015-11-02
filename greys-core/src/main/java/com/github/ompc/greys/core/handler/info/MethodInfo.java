package com.github.ompc.greys.core.handler.info;

import com.github.ompc.greys.core.GaMethod;
import com.github.ompc.greys.core.util.GaReflectUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 方法信息
 * Created by vlinux on 15/11/3.
 */
public class MethodInfo {

    private final TypeInfo declaringClass;
    private final TypeInfo returnClass;
    private final int modifier;
    private final String name;
    private final Collection<TypeInfo> annotations;
    private final Collection<TypeInfo> parameters;
    private final Collection<TypeInfo> exceptions;


    public MethodInfo(final GaMethod gaMethod) {
        this.declaringClass = new TypeInfo(gaMethod.getDeclaringClass());
        this.returnClass = new TypeInfo(gaMethod.getReturnType());
        this.modifier = GaReflectUtils.computeGaMethodModifier(gaMethod);
        this.name = gaMethod.getName();
        this.annotations = toAnnotationTypeInfos(gaMethod);
        this.parameters = toParameterTypeInfos(gaMethod);
        this.exceptions = toExceptionTypeInfos(gaMethod);
    }


    private Collection<TypeInfo> toAnnotationTypeInfos(final GaMethod gaMethod) {
        final Annotation[] annotationArray = gaMethod.getAnnotations();
        if (null == annotationArray) {
            return null;
        }
        final List<TypeInfo> annotationTypeInfos = new ArrayList<TypeInfo>();
        for (Annotation annotation : annotationArray) {
            annotationTypeInfos.add(new TypeInfo(annotation.getClass()));
        }
        return annotationTypeInfos;
    }

    private Collection<TypeInfo> toParameterTypeInfos(final GaMethod gaMethod) {
        final Class<?>[] parameterClassArray = gaMethod.getParameterTypes();
        if (null == parameterClassArray) {
            return null;
        }
        final List<TypeInfo> parameterTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> parameterClass : parameterClassArray) {
            parameterTypeInfos.add(new TypeInfo(parameterClass));
        }
        return parameterTypeInfos;
    }

    private Collection<TypeInfo> toExceptionTypeInfos(final GaMethod gaMethod) {
        final Class<?>[] exceptionClassArray = gaMethod.getExceptionTypes();
        if (null == exceptionClassArray) {
            return null;
        }
        final List<TypeInfo> exceptionTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> exceptionClass : exceptionClassArray) {
            exceptionTypeInfos.add(new TypeInfo(exceptionClass));
        }
        return exceptionTypeInfos;
    }

}
