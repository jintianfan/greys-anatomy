package com.github.ompc.greys.core.handler.info;

import com.github.ompc.greys.core.util.GaReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 类信息
 * Created by vlinux on 15/11/2.
 */
public class ClassInfo extends TypeInfo {

    private final TypeInfo[] interfaces;
    private final TypeInfo[] annotations;
    private final TypeInfo[] superClasses;
    private final ObjectInfo[] classLoaders;
    private final FieldInfo[] fieldInfos;

    public ClassInfo(
            final Class<?> clazz,
            final boolean isIncludeInterfaces,
            final boolean isIncludeAnnotations,
            final boolean isIncludeSuperClasses,
            final boolean isIncludeClassLoaders,
            final boolean isIncludeFields) {
        super(clazz);

        this.interfaces = isIncludeInterfaces
                ? toInterfaceClassInfo(clazz)
                : null;

        this.annotations = isIncludeAnnotations
                ? toAnnotationClassInfo(clazz)
                : null;

        this.superClasses = isIncludeSuperClasses
                ? toSuperClassClassInfo(clazz)
                : null;

        this.classLoaders = isIncludeClassLoaders
                ? toClassLoaderClassInfo(clazz)
                : null;

        this.fieldInfos = isIncludeFields
                ? toFieldInfo(clazz)
                : null;
    }

    private TypeInfo[] toInterfaceClassInfo(final Class<?> clazz) {
        final Class<?>[] interfaceArray = clazz.getInterfaces();
        if (null == interfaceArray) {
            return null;
        }
        final TypeInfo[] interfaceTypeInfoArray = new TypeInfo[interfaceArray.length];
        for (int index = 0; index < interfaceArray.length; index++) {
            interfaceTypeInfoArray[index] = new TypeInfo(interfaceArray[index]);
        }
        return interfaceTypeInfoArray;
    }

    private TypeInfo[] toAnnotationClassInfo(final Class<?> clazz) {
        final Annotation[] annotationArray = clazz.getAnnotations();
        if (null == annotationArray) {
            return null;
        }
        final TypeInfo[] annotationTypeInfoArray = new TypeInfo[annotationArray.length];
        for (int index = 0; index < annotationArray.length; index++) {
            annotationTypeInfoArray[index] = new TypeInfo(annotationArray[index].getClass());
        }
        return annotationTypeInfoArray;
    }

    private TypeInfo[] toSuperClassClassInfo(final Class<?> clazz) {
        final List<Class<?>> superClassList = GaReflectUtils.recGetSuperClass(clazz);
        final int length = superClassList.size();
        final TypeInfo[] superClassTypeInfoArray = new TypeInfo[length];
        for (int index = 0; index < length; index++) {
            superClassTypeInfoArray[index] = new TypeInfo(superClassList.get(index));
        }
        return superClassTypeInfoArray;
    }

    private ObjectInfo[] toClassLoaderClassInfo(final Class<?> clazz) {
        final List<ClassLoader> classLoaderList = GaReflectUtils.recGetClassLoader(clazz);
        final int length = classLoaderList.size();
        final ObjectInfo[] classLoaderObjectInfoArray = new ObjectInfo[length];
        for (int index = 0; index < length; index++) {
            classLoaderObjectInfoArray[index] = new ObjectInfo(classLoaderList.get(index));
        }
        return classLoaderObjectInfoArray;
    }

    private FieldInfo[] toFieldInfo(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        final int length = fields.length;
        final FieldInfo[] fieldInfoArray = new FieldInfo[length];
        for (int index = 0; index < length; index++) {
            fieldInfoArray[index] = new FieldInfo(fields[index]);
        }
        return fieldInfoArray;
    }

}
