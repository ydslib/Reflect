package com.yds.reflect;

/**
 * Created by yds
 * on 2020/4/14.
 */
public class NullReflect extends Reflect {
    private NULL _null = new NULL();

    public NullReflect(Reflect reflect) {
        super(reflect);
    }

    @Override
    public <T> T getWrapObject() {
        return (T) _null;
    }

    @Override
    public <T> T off() {
        return (T) _null;
    }


}
