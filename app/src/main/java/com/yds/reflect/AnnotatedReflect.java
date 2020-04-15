package com.yds.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * Created by yds
 * on 2020/4/14.
 */
public class AnnotatedReflect<A extends AnnotatedElement> extends Reflect {
    protected A value;
    private List<Annotation> annotations;
    private List<Annotation> declaredAnnotations;
    public AnnotatedReflect(Reflect reflect, A value){
        super(reflect);
        this.value = value;
    }

    @Override
    public A getWrapObject() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A off() {
        return value;
    }
}
