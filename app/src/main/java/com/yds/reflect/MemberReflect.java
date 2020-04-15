package com.yds.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

/**
 * Created by yds
 * on 2020/4/14.
 */
public class MemberReflect<M extends Member & AnnotatedElement> extends AnnotatedReflect<M> {
    protected Object receiver;
    public MemberReflect(Reflect reflect, M member) {
        this(reflect, member, null);
    }

    public MemberReflect(Reflect reflect, M member, Object receiver) {
        super(reflect, member);
        if (member != null) {
            super.clazz = member.getClass();
        }
        this.receiver = receiver;
    }

    /**
     *
     * @return 获取被包装的对象
     */
    @Override
    public M getWrapObject() {
        return super.getWrapObject();
    }
}
