package com.yds.reflect;

import android.text.TextUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by yds
 * on 2020/4/14.
 */
public abstract class Reflect {
    // 空数组避免过多临时对象
    protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    // 基本类型映射
    private static final Map<String, Class<?>> PRIMTYPEMAPPING = new HashMap<String, Class<?>>();

    static {
        PRIMTYPEMAPPING.put("int", int.class);
        PRIMTYPEMAPPING.put("string", String.class);
        PRIMTYPEMAPPING.put("char", char.class);
        PRIMTYPEMAPPING.put("byte", byte.class);
        PRIMTYPEMAPPING.put("short", short.class);
        PRIMTYPEMAPPING.put("float", float.class);
        PRIMTYPEMAPPING.put("long", long.class);
        PRIMTYPEMAPPING.put("double", double.class);
        PRIMTYPEMAPPING.put("boolean", boolean.class);
        PRIMTYPEMAPPING.put("void", void.class);
        PRIMTYPEMAPPING.put("class", Class.class);
    }

    protected Reflect reflect;
    protected Class<?> clazz;
    private ConcurrentMap<String, Field> fieldsMap = new ConcurrentHashMap<String, Field>();
    private ConcurrentMap<MethodKey, Method> methodMap = new ConcurrentHashMap<MethodKey, Method>();

    public Reflect(Reflect reflectUtils) {
        this.reflect = reflectUtils;
    }

    public static ObjectReflect from(Reflect reflect, Object object) throws ReflectException {
        return new ObjectReflect(reflect, object);
    }

    public static ClassReflect from(Class<?> clazz) throws ReflectException {
        if (clazz == null) {
            throw new ReflectException("Illegal argument:null");
        }
        return new ClassReflect(nullReflect(), clazz);
    }

    public static ClassReflect from(String name) throws ReflectException {
        if (name == null || name.length() < 1)
            throw new ReflectException("no characters!");
        return from(forName(name));
    }

    public static MethodReflect from(Method method, Object receiver) {
        if (method == null) {
            throw new ReflectException("method is null");
        }
        return new MethodReflect(nullReflect(), method, receiver);
    }

    /**
     * 反射静态方法
     *
     * @param method
     * @param arguments
     * @return
     */
    public static MethodReflect from(Method method, Object... arguments) {
        if (method == null) {
            throw new ReflectException("method is null");
        }
        return new MethodReflect(nullReflect(), method, null, arguments);
    }

    public static MethodReflect from(String methodName, Object receiver, Object... arguments) {
        if (TextUtils.isEmpty(methodName)) {
            throw new ReflectException("methodName is null");
        }
        Class<?>[] paramTypes = types(arguments);
        Method method = null;
        try {
            method = accessible(receiver.getClass().getDeclaredMethod(methodName, paramTypes));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return from(method, receiver, arguments);
    }

    public static MethodReflect from(Method method, Object receiver, Object... arguments) {
        if (method == null) {
            throw new ReflectException("method is null");
        }
        return new MethodReflect(nullReflect(), method, receiver, arguments);
    }

    public static MethodReflect from(Method method) {
        return from(method, EMPTY_OBJECT_ARRAY);
    }

    private static <T> Class<T> forName(String name) throws ReflectException {
        return forName(name, null);
    }

    private static <T> Class<T> forName(String name, ClassLoader loader) throws ReflectException {
        Class<?> primtype = PRIMTYPEMAPPING.get(name);
        if (primtype != null) {
            return (Class<T>) primtype;
        }
        // "java.lang.String[]" style arrays
        if (name.endsWith("[]")) {
            Class<?> emlClass = forName(name.substring(0, name.length() - 2));
            return (Class<T>) Array.newInstance(emlClass, 0).getClass();
        }
        // "[I" or "[Ljava.lang.String" style arrays
        if (name.startsWith("[L") || name.startsWith("[I")) {
            Class<?> elmClass = forName(name.substring(2), loader);
            return (Class<T>) Array.newInstance(elmClass, 0).getClass();
        }
        // "[[[I" or "[[[[Ljava.lang.String" style arrays
        if (name.charAt(0) == '[') {
            Class<?> elmClass = forName(name.substring(1), loader);
            return (Class<T>) Array.newInstance(elmClass, 0).getClass();
        }
        if (loader == null) {
            loader = getDefaultClassLoader();
        }

        try {
            return (Class<T>) (loader != null ? loader.loadClass(name) : Class.forName(name));
        } catch (ClassNotFoundException e) {
            int lastDotIndex = name.lastIndexOf(".");
            if (lastDotIndex != -1) {
                String clsName = name.substring(0, lastDotIndex) + "$" + name.substring(lastDotIndex + 1);
                try {
                    return (Class<T>) (loader != null ? loader.loadClass(clsName) : Class.forName(clsName));
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            throw new ReflectException(e);
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable t) {

        }
        if (cl == null) {
            cl = Reflect.class.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
        }
        return cl;
    }

    public static FieldReflect from(Field field) throws ReflectException {
        return from(field, null);
    }

    public static FieldReflect from(Field field, Object object) throws ReflectException {
        if (field == null) {
            throw new ReflectException("field is null");
        }
        return new FieldReflect(nullReflect(), field, object);
    }

    /**
     * 将访问受限的对象转为访问不受限的对象
     *
     * @param accessible 访问受限的对象
     * @return 如果accessible不为空，则为访问不受限的对象
     */
    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null)
            return null;
        if (accessible instanceof Member) {
            Member m = (Member) accessible;
            if (Modifier.isPublic(m.getModifiers())
                    && Modifier.isPublic(((Member) accessible).getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
        return accessible;
    }


    public Reflect createConstructor() throws ReflectException {
        return createConstructor(EMPTY_OBJECT_ARRAY);
    }

    public Reflect createConstructor(Object... arguments) throws ReflectException {
        Class<?>[] types = types(arguments);
        try {
            Constructor<?> constructor = getClazz().getDeclaredConstructor(types);
            return new ConstructorReflect(this, constructor).newInstance(this, arguments);
        } catch (NoSuchMethodException e) {
            for (Constructor<?> constructor : getClazz().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return new ConstructorReflect(this, constructor).newInstance(this, arguments);
                }
            }
            throw new ReflectException(e);
        }
    }

    public MethodReflect method(String methodName) throws ReflectException {
        return method(methodName, EMPTY_CLASS_ARRAY);
    }

    public MethodReflect method(String methodName, Class<?>... paramTypes) {
        Method method = method0(methodName, paramTypes);
        if (method == null) {
            return null;
        }
        return new MethodReflect(this, method, off());
    }

    private Method method0(String methodName, Class<?>... paramTypes) throws ReflectException {
        MethodKey key = new MethodKey(methodName, paramTypes);
        Method method = methodMap.get(key);
        if (method == null) {
            Class<?> clazz = getClazz();
            try {
                method = clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                while (clazz != null) {
                    try {
                        method = accessible(clazz.getDeclaredMethod(methodName, paramTypes));
                    } catch (NoSuchMethodException ignore) {

                    }
                    clazz = clazz.getSuperclass();
                }
            }
            methodMap.put(key, method);
        }
        return method;
    }

    public FieldReflect field(String fieldName) {
        Field field = field0(fieldName);
        if (field != null) {
            return new FieldReflect(this, field, off());
        } else {
            return null;
        }
    }

    private Field field0(String name) throws ReflectException {
        Field field = fieldsMap.get(name);
        if (field == null) {
            Class<?> clazz = getClazz();
            try {
                field = clazz.getField(name);
            } catch (NoSuchFieldException e) {
                while (clazz != null) {
                    try {
                        field = accessible(clazz.getDeclaredField(name));
                        break;
                    } catch (NoSuchFieldException ignore) {
                    }
                    clazz = clazz.getSuperclass();
                }
            }
            Field previous = fieldsMap.putIfAbsent(name, field);
            if (previous != null) {
                field = previous;
            }
        }
        return field;
    }

    /**
     * 方法参数类型匹配
     */
    private static boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)
                    continue;

                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
                    continue;

                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取包装类
     */
    public static Class<?> wrapper(Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }
        return type;
    }

    /**
     * 获取被包装的对象
     */
    public abstract <T> T getWrapObject();

    /**
     * 通过参数值获取参数类型
     *
     * @param values 参数值数组
     * @return 参数类型数组
     */
    private static Class<?>[] types(Object[] values) {
        if (values == null)
            return EMPTY_CLASS_ARRAY;
        Class<?>[] types = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            types[i] = values[i] == null ? NULL.class : values[i].getClass();
        }
        return types;
    }

    public final Class<?> getClazz() {
        return clazz;
    }

    protected static class NULL {
    }

    protected static Reflect nullReflect() {
        return new NullReflect(null);
    }

    /**
     * 打破final修饰，使字段可写
     */
    public static Field breakFinal(Field field) throws IllegalAccessException, NoSuchFieldException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("accessFlags");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(field);
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);
        return field;
    }

    /**
     * 获取被包装的对象
     */
    public abstract <T> T off();

    private static final class MethodKey {
        private String name;
        private Class<?>[] parameterTypes;
        private int hash;

        private MethodKey(String name, Class<?>[] parameterTypes) {
            super();
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.hash = hash0();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        private int hash0() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + Arrays.hashCode(parameterTypes);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MethodKey other = (MethodKey) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (!Arrays.equals(parameterTypes, other.parameterTypes))
                return false;
            return true;
        }
    }
}
