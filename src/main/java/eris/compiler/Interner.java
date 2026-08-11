package eris.compiler;

import java.util.HashMap;
import java.util.Map;

public class Interner<K, V> {
    private final Producer<K, V> producer;
    private final SideEffectExecutor<V> executor;

    private final Map<K, V> map = new HashMap<>();

    public Interner(Producer<K, V> producer, SideEffectExecutor<V> executor) {
        this.producer = producer;
        this.executor = executor;
    }

    public V get(K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }

        V value = producer.produce(key);
        if (executor != null) {
            executor.execute(value);
        }

        map.put(key, value);
        return value;
    }

    public interface Producer<K, V> {
        V produce(K key);
    }

    public interface SideEffectExecutor<V> {
        void execute(V value);
    }
}
