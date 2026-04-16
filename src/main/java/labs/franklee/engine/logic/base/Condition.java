package labs.franklee.engine.logic.base;

import labs.franklee.engine.logic.impl.AND;
import labs.franklee.engine.logic.path.Path;

public abstract class Condition extends Node implements Negatable<Condition> {

    public Condition() {
        this.type = NodeType.Condition;
    }

    @Override
    public Relation resolve() {
        return new AND(new Path().addCondition(this));
    }

}
