package labs.franklee.engine.logic.impl;

import dev.cel.bundle.Cel;
import dev.cel.common.types.SimpleType;
import dev.cel.runtime.CelRuntime;
import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Condition;
import labs.franklee.engine.logic.base.Constant;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;


public class EqualCondition extends Condition {

    private final static String EQUAL = " == ";

    private final String key;

    private final ValueType valueType;

    private final String value;

    private String expression;

    private Set<String> expressionValueVars;

    private CelRuntime.Program program;

    private Map<String, Object> builtinParams;

    public EqualCondition(String key, String value, ValueType valueType) {
        super();
        this.key = key;
        this.value = value;
        this.valueType = valueType;
    }

    @Override
    public Condition negate() {
        return new NotEqualCondition(this.key, this.value, this.valueType);
    }

    @Override
    public void before(Context context) {

    }

    @Override
    public boolean evaluate(Context context) {
        return false;
    }

    public void compile() throws Exception {
        if (this.valueType == ValueType.Expression) {
            this.expression = this.key + EQUAL + this.value;
            this.expressionValueVars = CelUtils.extractTopVarNames(this.expression);
            Cel cel = CelUtils.buildCelWithVars(this.expressionValueVars, null);
            this.program = CelUtils.buildProgram(this.expression, cel);
        } else if (this.valueType == ValueType.String) {
            String valKey = Constant.BUILTIN_KEY + "STR_001";
            this.expression = this.key + EQUAL + valKey;
            this.expressionValueVars = CelUtils.extractTopVarNames(this.expression);
            Cel cel = CelUtils.buildCelWithVars(this.expressionValueVars, Map.of(valKey, SimpleType.STRING));
            this.program = CelUtils.buildProgram(this.expression, cel);
            this.builtinParams = Map.of(valKey, this.value);
        } else if (this.valueType == ValueType.Boolean) {
            boolean b = Boolean.parseBoolean(this.value);
            if (b) {
                this.expression = this.key + EQUAL + "true";
            } else {
                this.expression = this.key + EQUAL + "false";
            }
            this.expressionValueVars = CelUtils.extractTopVarNames(this.expression);
            Cel cel = CelUtils.buildCelWithVars(this.expressionValueVars, null);
            this.program = CelUtils.buildProgram(this.expression, cel);
        } else if (this.valueType == ValueType.Number) {
            String valKey = Constant.BUILTIN_KEY + "NUM_001";
            this.expression = this.key + EQUAL + valKey;
            this.expressionValueVars = CelUtils.extractTopVarNames(this.expression);

            BigDecimal bd = new BigDecimal(this.value);
            BigDecimal striped = bd.stripTrailingZeros();
            Cel cel;
            if (striped.scale() <= 0) {
                // whole number → long, exact
                cel = CelUtils.buildCelWithVars(this.expressionValueVars, Map.of(valKey, SimpleType.INT));
                this.builtinParams = Map.of(valKey, striped.longValueExact());
            } else {
                // decimal → double, IEEE 754 precision
                cel = CelUtils.buildCelWithVars(this.expressionValueVars, Map.of(valKey, SimpleType.DOUBLE));
                this.builtinParams = Map.of(valKey, striped.doubleValue());
            }
            this.program = CelUtils.buildProgram(this.expression, cel);
        } else {
            throw new UnsupportedOperationException("Unsupported ValueType");
        }
    }

}
