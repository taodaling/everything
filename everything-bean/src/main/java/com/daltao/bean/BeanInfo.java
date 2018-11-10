package com.daltao.bean;

import com.daltao.cache.Cache;
import com.daltao.cache.ConcurrentReferenceCache;
import com.daltao.cache.LazyInitCache;
import com.daltao.collection.UnmodifiableArrayIterator;
import com.daltao.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.sql.Ref;
import java.util.*;

public final class BeanInfo {
    private Class beanClass;
    private Attribute[] attributes;
    private Map<String, Attribute> attributeMap;
    private static Cache<Class, BeanInfo> cache =
            new LazyInitCache<>(new ConcurrentReferenceCache(), BeanInfo::new);

    private BeanInfo(Class beanClass) {
        this.beanClass = beanClass;

        Map<String, Class> setterMap = new LinkedHashMap<>();
        Map<String, Class> getterMap = new LinkedHashMap<>();
        for (Method method : beanClass.getMethods()) {

            if (method.getName().startsWith("set")) {
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1) {
                    setterMap.put(ReflectionUtils.getSetterAttributeName(method.getName()), paramTypes[0]);
                }
            } else if (method.getName().startsWith("get")) {
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0) {
                    getterMap.put(ReflectionUtils.getGetterAttributeName(method.getName()), paramTypes[0]);
                }
            }
        }

        attributeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Class> entry : setterMap.entrySet()) {
            String name = entry.getKey();
            Class type = entry.getValue();
            attributeMap.put(name, new Attribute(name, type, getterMap.containsKey(name), true));
        }
        for (Map.Entry<String, Class> entry : getterMap.entrySet()) {
            String name = entry.getKey();
            Class type = entry.getValue();
            if (attributeMap.containsKey(name)) {
                continue;
            }
            attributeMap.put(name, new Attribute(name, type, true, false));
        }

        attributes = attributeMap.values().toArray(new Attribute[attributes.length]);
        Arrays.sort(attributes);
    }

    public Iterator<Attribute> iterator() {
        return new UnmodifiableArrayIterator(attributes, 0, attributes.length);
    }

    public int getAttributeNumber() {
        return attributes.length;
    }

    public Attribute getAttribute(int i) {
        return attributes[i];
    }

    public Attribute getAttribute(String name) {
        return attributeMap.get(name);
    }

    public static BeanInfo getInstance(Class beanClass) {
        return cache.get(beanClass);
    }
}
