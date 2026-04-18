package labs.franklee.celero.context;

import labs.franklee.celero.exceptions.InvalidParameterException;
import labs.franklee.celero.logic.base.EvalResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Context {

    public static class Builder {
        private Map<String, Object> params;
        private boolean enableMissState = false;

        private Builder(Map<String, Object> params) {
            this.params = params;
        }

        public static Builder createBuilder(Map<String, Object> params) {
            return new Builder(params);
        }

        public Builder enableMissState() {
            this.enableMissState = true;
            return this;
        }

        public Context build() {
            Context context = new Context(this.params);
            context.enableMissing = this.enableMissState;
            return context;
        }
    }

    private final Map<String, Object> params;

    private Map<String, Object> evalParams;

    private boolean enableMissing;

    private Map<String, EvalResult> conditionEvalResult = new HashMap<>();

    private Context(Map<String, Object> map) {
        if (null == map) {
            this.params = new HashMap<>();
            return;
        }
        // remove null key and null value
        map.remove(null);
        Set<String> keys = new HashSet<>();
        map.keySet().forEach(k -> {
            if (k.startsWith("_")) {
                throw new InvalidParameterException("param keys can not start with _ [key=" + k + "]");
            }
            if (null == map.get(k)) {
                keys.add(k);
            }
        });
        keys.forEach(map::remove);
        this.params = map;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void buildEvalParams(Map<String, Object> builtinParam) {
        Map<String, Object> map = new HashMap<>(this.params);
        if (null != builtinParam) {
            builtinParam.keySet().forEach(k -> {
                if (null == k) {
                    throw new InvalidParameterException("builtin keys must not be null");
                }
                if (!k.startsWith("_")) {
                    throw new InvalidParameterException("builtin keys must start with _ [key=" + k + "]");
                }
            });
            map.putAll(builtinParam);
        }
        this.evalParams = map;
    }

    public void setConditionEvalResult(String id, EvalResult result) {
        this.conditionEvalResult.putIfAbsent(id, result);
    }

    public Boolean getConditionResult(String id) {
        EvalResult evalResult = this.conditionEvalResult.get(id);
        return null == evalResult ? null : evalResult.isTrue();
    }

    public EvalResult getConditionEvalResult(String id) {
        return this.conditionEvalResult.get(id);
    }

    public Map<String, Object> getEvalParam() {
        return this.evalParams;
    }

    public boolean isEnableMissing() {
        return enableMissing;
    }
}
