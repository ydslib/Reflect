package com.yds.reflect;

/**
 * Created by yds
 * on 2020/4/14.
 */
public class ObjectReflect extends Reflect {
    private Object object;

    public ObjectReflect(Reflect reflect, Object object) {
        super(reflect);
        this.object = object;
        if (object != null) {
            super.clazz = object.getClass();
        } else {
            super.clazz = NULL.class;
        }
    }

    @Override
    public <T> T getWrapObject() {
        return (T) object;
    }

    @Override
    public <T> T off() {
        return (T) object;
    }
}
