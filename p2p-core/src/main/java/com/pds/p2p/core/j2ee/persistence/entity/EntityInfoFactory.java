/**
 *
 */
package com.pds.p2p.core.j2ee.persistence.entity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author 王文
 * @date 2015-8-6 下午2:20:32
 */
public class EntityInfoFactory {

    private final static ConcurrentMap<Class<?>, EntityInfo<?>> entityInfoMap =
            new ConcurrentHashMap<Class<?>, EntityInfo<?>>();

    @SuppressWarnings("unchecked")
    public static <T> EntityInfo<T> forEntityClass(Class<T> domainClass) {
        EntityInfo<T> a = (EntityInfo<T>) entityInfoMap.get(domainClass);
        if (a == null) {
            synchronized(entityInfoMap) {
                a = (EntityInfo<T>) entityInfoMap.get(domainClass);
                if (a == null) {
                    a = new EntityInfo<>(domainClass);
                    entityInfoMap.put(domainClass, a);
                }
            }
        }
        return a;
    }
}
