package com.github.ompc.greys.core.handler.info;

import com.github.ompc.greys.core.util.GaReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类信息
 * Created by vlinux on 15/11/2.
 */
public class ClassInfo extends TypeInfo {

    private final Collection<TypeInfo> interfaces;
    private final Collection<TypeInfo> annotations;
    private final Collection<TypeInfo> superClasses;
    private final Collection<ObjectInfo> classLoaders;
    private final Collection<FieldInfo> fieldInfos;

    public ClassInfo(
            final Class<?> clazz,
            final boolean isIncludeInterfaces,
            final boolean isIncludeAnnotations,
            final boolean isIncludeSuperClasses,
            final boolean isIncludeClassLoaders,
            final boolean isIncludeFields) {
        super(clazz);

        this.interfaces = isIncludeInterfaces
                ? toInterfaceTypeInfos(clazz)
                : null;

        this.annotations = isIncludeAnnotations
                ? toAnnotationTypeInfos(clazz)
                : null;

        this.superClasses = isIncludeSuperClasses
                ? toSuperClassTypeInfos(clazz)
                : null;

        this.classLoaders = isIncludeClassLoaders
                ? toClassLoaderObjectInfos(clazz)
                : null;

        this.fieldInfos = isIncludeFields
                ? toFieldInfos(clazz)
                : null;
    }

    private Collection<TypeInfo> toInterfaceTypeInfos(final Class<?> clazz) {
        final Class<?>[] interfaceArray = clazz.getInterfaces();
        if (null == interfaceArray) {
            return null;
        }
        final List<TypeInfo> interfaceTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> interfaceClass : interfaceArray) {
            interfaceTypeInfos.add(new TypeInfo(interfaceClass));
        }
        return interfaceTypeInfos;
    }

    private Collection<TypeInfo> toAnnotationTypeInfos(final Class<?> clazz) {
        final Annotation[] annotationArray = clazz.getAnnotations();
        if (null == annotationArray) {
            return null;
        }
        final List<TypeInfo> annotationTypeInfos = new ArrayList<TypeInfo>();
        for (Annotation annotation : annotationArray) {
            annotationTypeInfos.add(new TypeInfo(annotation.getClass()));
        }
        return annotationTypeInfos;
    }

    private Collection<TypeInfo> toSuperClassTypeInfos(final Class<?> clazz) {
        final List<Class<?>> superClassList = GaReflectUtils.recGetSuperClass(clazz);
        final List<TypeInfo> superClassTypeInfos = new ArrayList<TypeInfo>();
        for (Class<?> superClass : superClassList) {
            superClassTypeInfos.add(new TypeInfo(superClass));
        }
        return superClassTypeInfos;
    }

    private Collection<ObjectInfo> toClassLoaderObjectInfos(final Class<?> clazz) {
        final List<ClassLoader> classLoaderList = GaReflectUtils.recGetClassLoader(clazz);
        final List<ObjectInfo> classLoaderObjectInfos = new ArrayList<ObjectInfo>();
        for (ClassLoader classLoader : classLoaderList) {
            classLoaderObjectInfos.add(new ObjectInfo(classLoader));
        }
        return classLoaderObjectInfos;
    }

    private Collection<FieldInfo> toFieldInfos(final Class<?> clazz) {
        final Field[] fieldArray = clazz.getDeclaredFields();
        if (null == fieldArray) {
            return null;
        }
        final List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
        for (Field field : fieldArray) {
            fieldInfos.add(new FieldInfo(field));
        }
        return fieldInfos;
    }

}
