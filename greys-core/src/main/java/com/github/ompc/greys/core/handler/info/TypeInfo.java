package com.github.ompc.greys.core.handler.info;

import com.github.ompc.greys.core.util.GaReflectUtils;

import java.security.CodeSource;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Created by vlinux on 15/11/2.
 */
public class TypeInfo {

    private final String name;
    private final String codeSource;
    private final int modifier;
    private final int type;

    public TypeInfo(Class<?> clazz) {

        this.name = clazz.getName();
        this.codeSource = getCodeSource(clazz);
        this.modifier = GaReflectUtils.computeClassModifier(clazz);
        this.type = GaReflectUtils.computeClassType(clazz);

    }

    private String getCodeSource(final Class<?> clazz) {
        final CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        if (null == cs
                || null == cs.getLocation()
                || null == cs.getLocation().getFile()) {
            return EMPTY;
        }
        return cs.getLocation().getFile();
    }
}
