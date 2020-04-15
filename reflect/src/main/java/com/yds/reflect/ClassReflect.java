package com.yds.reflect;

/**
 * Created by yds
 * on 2020/4/14.
 */
public class ClassReflect extends AnnotatedReflect<Class<?>> {
    public ClassReflect(Reflect reflect, Class<?> clazz) {
        super(reflect, clazz);
        super.clazz = clazz;
    }

    @Override
    public Class<?> off() {
        return super.clazz;
    }

}
