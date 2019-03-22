package com.huawei.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DenseMap<K extends Integer, V> implements Map<K, V> {
    int offset;
    V[] values;

    private DenseMap(int offset, V[] values) {
        this.offset = offset;
        this.values = values;
    }

    public static <K, V> DenseMap wrap(int offset, V[] values) {
        return new DenseMap<>(offset, values);
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return values.length > 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof Integer) {
            int intKey = (Integer) key;
            return intKey > offset && intKey < offset + values.length;
        }else
            return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return Arrays.asList(values).contains(value);
    }

    // TODO: not completed
    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
