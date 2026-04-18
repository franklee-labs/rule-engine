package labs.franklee.celero.logic.base;

import dev.cel.common.CelErrorCode;
import dev.cel.runtime.CelEvaluationException;
import dev.cel.runtime.CelRuntime;
import dev.cel.runtime.CelUnknownSet;
import labs.franklee.celero.context.Context;
import labs.franklee.celero.exceptions.EvalException;
import labs.franklee.celero.exceptions.InvalidConditionException;
import labs.franklee.celero.exceptions.MissingParameterException;
import labs.franklee.celero.logic.impl.AND;
import labs.franklee.celero.logic.path.Path;

public abstract class Condition extends Node implements Negatable<Condition> {

    private final int priority;

    protected String expression = "";

    public Condition() {
        this.type = NodeType.Condition;
        this.priority = Priority.DEFAULT;
    }

    public Condition(int priority) {
        this.type = NodeType.Condition;
        this.priority = priority;
    }

    public final int getPriority() {
        return this.priority;
    }

    public boolean validate() {
        return true;
    }

    public void compile() throws Exception {}

    public String getExpression() {
        return this.expression;
    }

    public final void build() throws Exception {
        if (!this.validate()) {
            throw new InvalidConditionException();
        }
        this.compile();
    }

    @Override
    public final Relation resolve() {
        return new AND(new Path().addCondition(this));
    }

    public final EvalResult execute(Context context) {
        this.before(context);
        EvalResult result = null;
        try {
            result = this.evaluate(context) ? EvalResult.TRUE : EvalResult.FALSE;
        } catch (MissingParameterException e) {
            result = context.isEnableMissing() ? EvalResult.MISS : EvalResult.FALSE;
        } finally {
            context.setConditionEvalResult(this.getId(), result);
            this.after(context);
        }
        return result;
    }

    public void before(Context context) {}

    public abstract boolean evaluate(Context context) throws MissingParameterException;

    public void after(Context context) {}

    protected boolean celEvaluate(CelRuntime.Program program, Context context) throws MissingParameterException {
        try {
            Object result = program.eval(context.getEvalParam());
            if (result instanceof CelUnknownSet) {
                throw new MissingParameterException("missing parameter in expression: " + this.getExpression());
            }
            return result instanceof Boolean b && b;
        } catch (CelEvaluationException e) {
            if (e.getErrorCode() == CelErrorCode.ATTRIBUTE_NOT_FOUND) {
                throw new MissingParameterException(e.getMessage(), e);
            }
            throw new EvalException(e);
        }
    }
}
