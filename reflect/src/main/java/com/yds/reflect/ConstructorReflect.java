package com.yds.reflect;

import java.lang.reflect.Constructor;

/**
 * Created by yds
 * on 2020/4/14.
 */
public class ConstructorReflect extends MemberReflect<Constructor<?>> {
    private Object[] arguments = EMPTY_OBJECT_ARRAY;
    public ConstructorReflect(Reflect reflect, Constructor<?> constructor) {
        super(reflect, constructor);
    }

    public Reflect newInstance() throws ReflectException {
        return newInstance(arguments);
    }

    protected Reflect newInstance(Reflect reflect, Object... arguments) throws ReflectException {
        try {
            return ObjectReflect.from(reflect, accessible(super.getWrapObject()).newInstance(arguments));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }
    public Reflect newInstance(Object... arguments) throws ReflectException {
        try {
            return ObjectReflect.from(this, accessible(super.getWrapObject()).newInstance(arguments));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    @Override
    public Constructor<?> off() {
        return super.off();
    }
}
