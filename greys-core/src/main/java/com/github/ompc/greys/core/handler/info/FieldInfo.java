package com.github.ompc.greys.core.handler.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 属性信息
 * Created by vlinux on 15/11/2.
 */
public class FieldInfo {

    private final int modifier;
    private final String name;
    private final TypeInfo returnType;
    private final TypeInfo[] annotations;
    private final ObjectInfo value;

    public FieldInfo(final Field field) {

        this.modifier = field.getModifiers();
        this.name = field.getName();
        this.returnType = new TypeInfo(field.getType());
        this.annotations = toAnnotationTypeInfo(field);
        this.value = toValueObjectInfo(field);
    }

    private ObjectInfo toValueObjectInfo(final Field field) {
        if (Modifier.isStatic(field.getModifiers())) {
            try {
                return new ObjectInfo(field.get(null));
            } catch (Throwable t) {
                return null;
            }
        } else {
            return null;
        }
    }

    private TypeInfo[] toAnnotationTypeInfo(final Field field) {
        final Annotation[] annotationArray = field.getAnnotations();
        if (null == annotationArray) {
            return null;
        }
        final TypeInfo[] annotationTypeInfoArray = new TypeInfo[annotationArray.length];
        for (int index = 0; index < annotationArray.length; index++) {
            annotationTypeInfoArray[index] = new TypeInfo(annotationArray[index].getClass());
        }
        return annotationTypeInfoArray;
    }

}
