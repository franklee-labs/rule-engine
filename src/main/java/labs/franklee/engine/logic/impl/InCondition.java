package labs.franklee.engine.logic.impl;

import dev.cel.bundle.Cel;
import dev.cel.common.types.ListType;
import dev.cel.common.types.SimpleType;
import dev.cel.runtime.CelRuntime;
import labs.franklee.engine.context.Context;
import labs.franklee.engine.exceptions.EvalException;
import labs.franklee.engine.exceptions.InvalidConditionException;
import labs.franklee.engine.logic.base.Condition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates whether a field value is contained in a given list.
 *
 * The list supports mixed types: String, Long, Double, and Boolean.
 *
 * Example:
 * <pre>
 *   new InCondition("role", List.of("admin", "ops"))
 *   new InCondition("age",  List.of(18L, 25L, 30L))
 *   new InCondition("score", List.of(99.5, 88.0))
 *   new InCondition("flag", List.of(true, false))
 *   new InCondition("mixed", List.of("admin", 18L, 99.5, true))
 * </pre>
 */
public class InCondition extends Condition {

    private static final String LIST_KEY = Constant.BUILTIN_KEY + "LIST_001";

    private static final String IN = " in ";

    private final String key;
    private final List<Object> values;

    private String expression;
    private Set<String> expressionValueVars;
    private CelRuntime.Program program;
    private Map<String, Object> builtinParams;

    public InCondition(String key, List<Object> values) {
        super();
        this.setName("InCondition");
        this.key = key;
        this.values = values;
    }

    @Override
    public Condition negate() throws Exception {
        Condition condition = new NotInCondition(this.key, this.values);
        if (!condition.validate()) {
            throw new InvalidConditionException();
        }
        condition.compile();
        return condition;
    }

    @Override
    public boolean validate() {
        return this.values != null && !this.values.isEmpty();
    }

    @Override
    public void before(Context context) {
        context.buildEvalParams(this.builtinParams);
    }

    @Override
    public boolean evaluate(Context context) {
        try {
            Object eval = this.program.eval(context.getEvalParam());
            return eval instanceof Boolean b && b;
        } catch (Throwable e) {
            throw new EvalException(e);
        }
    }

    @Override
    public void compile() throws Exception {
        this.expression = this.key + IN + LIST_KEY;
        List<Object> normalized = this.values.stream().map(CelUtils::toCelValue).toList();
        this.builtinParams = Map.of(LIST_KEY, normalized);
        this.expressionValueVars = CelUtils.extractTopVarNames(this.expression);
        Cel cel = CelUtils.buildCelWithVars(
                this.expressionValueVars,
                Map.of(LIST_KEY, ListType.create(SimpleType.DYN))
        );
        this.program = CelUtils.buildProgram(this.expression, cel);
    }
}
