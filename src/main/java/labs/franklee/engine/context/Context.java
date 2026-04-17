package labs.franklee.engine.context;

import java.util.Map;

public class Context {

    private final Map<String, Object> params;

    private boolean result;

    public Context(Map<String, Object> params) {
        this.params = Map.copyOf(params);
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
