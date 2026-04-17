package labs.franklee.engine.logic.impl;

import dev.cel.runtime.CelRuntime;
import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Condition;

public class CelCondition extends Condition {

    private final String expression;
    private final CelRuntime.Program program;

    public CelCondition(String expression) {
        this.setName("CelCondition");
        try {
            this.expression = expression;
            this.program = CelUtils.buildProgram(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid CEL expression: " + expression, e);
        }
    }



    @Override
    public Condition negate() {
        return new NegateCelCondition(this);
    }

    @Override
    public boolean evaluate(Context context) {
        try {
            Object result = program.eval(context.getParams());
            return result instanceof Boolean b && b;
        } catch (Exception e) {
            throw new RuntimeException("CEL evaluation failed for: " + expression, e);
        }
    }

    public String getExpression() {
        return expression;
    }


}
