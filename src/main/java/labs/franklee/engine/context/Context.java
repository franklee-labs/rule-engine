package labs.franklee.engine.context;

import labs.franklee.engine.exceptions.InvalidParameterException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Context {

    private final Map<String, Object> params;

    private boolean result;

    public Context(Map<String, Object> map) {
        // remove null key and null value
        Map<String, Object> mutableMap = new HashMap<>(map);
        mutableMap.remove(null);
        Set<String> keys = new HashSet<>();
        mutableMap.keySet().forEach(k -> {
            if (k.startsWith("_")) {
                throw new InvalidParameterException("param key can not start with _. key=" + k);
            }
            if (null == mutableMap.get(k)) {
                keys.add(k);
            }
        });
        keys.forEach(mutableMap::remove);
        this.params = Map.copyOf(mutableMap);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Object get(String key) {
        return params.get(key);
    }

    public boolean result() {
        return this.result;
    }
}
