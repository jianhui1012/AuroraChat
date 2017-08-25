package com.golike.customviews.common;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/8/16.
 */

public class WeakValueHashMap extends AbstractMap implements Map {
    private Map hash;
    private ReferenceQueue queue;

    public Set entrySet() {
        this.processQueue();
        return this.hash.entrySet();
    }

    private void processQueue() {
        WeakValueHashMap.WeakValueRef ref;
        while((ref = (WeakValueHashMap.WeakValueRef)this.queue.poll()) != null) {
            if(ref == (WeakValueHashMap.WeakValueRef)this.hash.get(ref.key)) {
                this.hash.remove(ref.key);
            }
        }

    }

    public WeakValueHashMap(int initialCapacity, float loadFactor) {
        this.queue = new ReferenceQueue();
        this.hash = new HashMap(initialCapacity, loadFactor);
    }

    public WeakValueHashMap(int initialCapacity) {
        this.queue = new ReferenceQueue();
        this.hash = new HashMap(initialCapacity);
    }

    public WeakValueHashMap() {
        this.queue = new ReferenceQueue();
        this.hash = new HashMap();
    }

    public WeakValueHashMap(Map t) {
        this(Math.max(2 * t.size(), 11), 0.75F);
        this.putAll(t);
    }

    public int size() {
        this.processQueue();
        return this.hash.size();
    }

    public boolean isEmpty() {
        this.processQueue();
        return this.hash.isEmpty();
    }

    public boolean containsKey(Object key) {
        this.processQueue();
        return this.hash.containsKey(key);
    }

    public Object get(Object key) {
        this.processQueue();
        WeakReference ref = (WeakReference)this.hash.get(key);
        return ref != null?ref.get():null;
    }

    public Object put(Object key, Object value) {
        this.processQueue();
        Object rtn = this.hash.put(key, WeakValueHashMap.WeakValueRef.create(key, value, this.queue));
        if(rtn != null) {
            rtn = ((WeakReference)rtn).get();
        }

        return rtn;
    }

    public Object remove(Object key) {
        this.processQueue();
        return this.hash.remove(key);
    }

    public void clear() {
        this.processQueue();
        this.hash.clear();
    }

    private static class WeakValueRef extends WeakReference {
        public Object key;

        private WeakValueRef(Object key, Object val, ReferenceQueue q) {
            super(val, q);
            this.key = key;
        }

        private static WeakValueHashMap.WeakValueRef create(Object key, Object val, ReferenceQueue q) {
            return val == null?null:new WeakValueHashMap.WeakValueRef(key, val, q);
        }
    }
}
