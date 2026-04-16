package labs.franklee.engine.logic.path;

import labs.franklee.engine.logic.base.Condition;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private final List<Condition> conditions = new ArrayList<>();

    public Path addCondition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public Path addAllCondition(List<Condition> conditions) {
        this.conditions.addAll(conditions);
        return this;
    }

    public List<Condition> conditions() {
        return this.conditions;
    }
}
