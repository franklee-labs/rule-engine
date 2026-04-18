package labs.franklee.celero.logic.impl;

import dev.cel.runtime.CelRuntime;
import labs.franklee.celero.context.Context;
import labs.franklee.celero.exceptions.MissingParameterException;
import labs.franklee.celero.logic.base.Condition;

public class NegateCelCondition extends Condition {

    private final CelCondition origin;
    private CelRuntime.Program program;
    private final String expression;

    NegateCelCondition(CelCondition origin) {
        this.setName("NegateCelCondition");
        this.origin = origin;
        this.expression = "!(" + origin.getExpression() + ")";
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public Condition negate() {
        return origin;
    }

    @Override
    public void compile() throws Exception {
        this.program = CelUtils.buildProgram(this.expression);
    }

    @Override
    public boolean evaluate(Context context) throws MissingParameterException {
        return this.celEvaluate(this.program, context);
    }
}
