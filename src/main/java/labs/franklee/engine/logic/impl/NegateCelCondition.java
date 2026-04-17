package labs.franklee.engine.logic.impl;

import dev.cel.runtime.CelRuntime;
import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Condition;

public class NegateCelCondition extends Condition {

    private final CelCondition origin;
    private final CelRuntime.Program program;
    private final String expression;

    NegateCelCondition(CelCondition origin) {
        this.setName("NegateCelCondition");
        try {
            this.origin = origin;
            this.expression = "!(" + origin.getExpression() + ")";
            this.program = CelUtils.buildProgram(this.expression);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to negate CEL expression: " + origin.getExpression(), e);
        }
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public Condition negate() {
        return origin;
    }

    @Override
    public boolean evaluate(Context context) {
        try {
            Object result = program.eval(context.getParams());
            return result instanceof Boolean b && b;
        } catch (Exception e) {
            throw new RuntimeException(
                    "CEL evaluation failed for: !(" + origin.getExpression() + ")", e);
        }
    }
}
