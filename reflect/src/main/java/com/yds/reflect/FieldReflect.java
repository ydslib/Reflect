package com.yds.reflect;


import java.lang.reflect.Field;

/**
 * Created by yds
 * on 2020/4/15.
 */
public class FieldReflect extends MemberReflect<Field> {
    public FieldReflect(Reflect reflect, Field field) {
        super(reflect, field);
    }

    public FieldReflect(Reflect reflect, Field field, Object receiver) {
        super(reflect, field, receiver);
    }

    public FieldReflect set(Object fieldValue) {
        try {
            getWrapObject().set(super.receiver, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public Reflect get() {
        return from(this, getValue(super.receiver));
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Object object) throws ReflectException {
        try {
            return (T) (accessible(super.off()).get(object));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    @Override
    public Field off() {
        return super.off();
    }

}
