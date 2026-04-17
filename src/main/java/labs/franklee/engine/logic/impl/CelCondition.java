package labs.franklee.engine.logic.impl;

import dev.cel.runtime.CelRuntime;
import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Condition;

public class CelCondition extends Condition {

    private final String expression;
    private CelRuntime.Program program;

    public CelCondition(String expression) {
        this.setName("CelCondition");
        this.expression = expression;
    }

    @Override
    public void compile() throws Exception {
        this.program = CelUtils.buildProgram(expression);
    }

    @Override
    public Condition negate() throws Exception {
        Condition condition = new NegateCelCondition(this);
        condition.build();
        return condition;
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
