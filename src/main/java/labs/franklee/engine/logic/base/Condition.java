package labs.franklee.engine.logic.base;

import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.impl.AND;
import labs.franklee.engine.logic.path.Path;

public abstract class Condition extends Node implements Negatable<Condition> {

    private final int priority;

    public Condition() {
        this.type = NodeType.Condition;
        this.priority = Priority.DEFAULT;
    }

    public Condition(int priority) {
        this.type = NodeType.Condition;
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean validate() {
        return true;
    }

    public void compile() throws Exception {}

    @Override
    public Relation resolve() {
        return new AND(new Path().addCondition(this));
    }

    public boolean execute(Context context) {
        this.before(context);
        boolean result = this.evaluate(context);
        this.after(context, result);
        return result;
    }

    public void before(Context context) {}

    public abstract boolean evaluate(Context context);

    public void after(Context context, boolean result) {}
}
