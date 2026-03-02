package com.example.metrics;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global metrics registry — a proper thread-safe Singleton.
 *
 * Uses the Bill Pugh (static inner holder) pattern for lazy, thread-safe init.
 * Blocks reflection attacks and preserves identity across serialization.
 */
public class MetricsRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Long> counters = new HashMap<>();

    // Flag to block reflection-based construction
    private static boolean instanceCreated = false;

    // Private constructor — blocks reflection if instance already exists
    private MetricsRegistry() {
        if (instanceCreated) {
            throw new RuntimeException("Cannot create a second instance via reflection!");
        }
        instanceCreated = true;
    }

    // Bill Pugh Singleton: static inner class loaded lazily by the JVM
    private static class Holder {
        private static final MetricsRegistry INSTANCE = new MetricsRegistry();
    }

    public static MetricsRegistry getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized void setCount(String key, long value) {
        counters.put(key, value);
    }

    public synchronized void increment(String key) {
        counters.put(key, getCount(key) + 1);
    }

    public synchronized long getCount(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public synchronized Map<String, Long> getAll() {
        return Collections.unmodifiableMap(new HashMap<>(counters));
    }

    // Preserve singleton across serialization/deserialization
    @Serial
    private Object readResolve() {
        return getInstance();
    }
}
