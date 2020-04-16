package com.yds.reflect;

import java.lang.reflect.Method;

/**
 * Created by yds
 * on 2020/4/15.
 */
public class MethodReflect extends MemberReflect<Method> {
    private Object[] arguments = EMPTY_OBJECT_ARRAY;
    private Class<?>[] parameterTypes;

    public MethodReflect(Reflect reflect, Method method) {
        super(reflect, method);
    }

    public MethodReflect(Reflect reflect, Method method, Object receiver) {
        super(reflect, method, receiver);
    }

    public MethodReflect(Reflect reflect, Method method, Object receiver, Object[] arguments) {
        super(reflect, method, receiver);
        if (arguments != null) {
            this.arguments = arguments;
        }
    }

    public Class<?>[] getParameterTypes() {
        if (parameterTypes == null) {
            parameterTypes = this.value.getParameterTypes();
        }
        return parameterTypes;
    }

    public Reflect invoke() throws ReflectException {
        return invokeBy(this, super.receiver, this.arguments);
    }

    public Reflect invoke(Object... arguments) {
        return invokeBy(this, receiver, arguments);
    }

    public Reflect invokeBy(Object receiver, Object... arguments) {
        return invokeBy(this, receiver, arguments);
    }

    private Reflect invokeBy(Reflect reflect, Object receiver, Object... arguments) throws ReflectException {
        if (receiver == null || arguments == null || reflect == null) {
            throw new NullPointerException("the reflect of class object or parameters should not null");
        }
        try {
            Object retMethod = accessible(super.off()).invoke(receiver, arguments);
            if (retMethod == null) {
                return new NullReflect(reflect);
            }
            return from(reflect, retMethod);
        } catch (ReflectException e) {
            throw e;
        } catch (Exception e) {
            throw new ReflectException();
        }
    }

    @Override
    public Method off() {
        return super.off();
    }


}
