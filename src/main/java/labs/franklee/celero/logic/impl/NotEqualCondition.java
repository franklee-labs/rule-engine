package labs.franklee.celero.logic.impl;

import dev.cel.bundle.Cel;
import dev.cel.common.types.CelType;
import dev.cel.common.types.SimpleType;
import dev.cel.runtime.CelRuntime;
import labs.franklee.celero.context.Context;
import labs.franklee.celero.exceptions.EvalException;
import labs.franklee.celero.logic.base.Condition;
import labs.franklee.celero.logic.base.ValueType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class NotEqualCondition extends Condition {

    private final static String NOT_EQUAL = " != ";

    private final String key;

    private final ValueType valueType;

    private final String value;

    private String expression;

    private Set<String> expressionValueVars;

    private CelRuntime.Program program;

    private Map<String, Object> builtinParams;

    public NotEqualCondition(String key, String value, ValueType valueType) {
        super();
        this.setName("NotEqualCondition");
        this.key = key;
        this.value = value;
        this.valueType = valueType;
    }

    @Override
    public boolean validate() {
        if (this.valueType == ValueType.Number) {
            try {
                BigDecimal bd = new BigDecimal(this.value);
                return true;
            } catch (Throwable e) {
                return false;
            }
        }
        return super.validate();
    }

    @Override
    public Condition negate() throws Exception {
        Condition condition = new EqualCondition(this.key, this.value, this.valueType);
        condition.build();
        return condition;
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

    private void buildCelProgram(Map<String, CelType> celTypes) throws Exception {
        this.expressionValueVars = CelUtils.extractTopVarNames(this.expression);
        Cel cel = CelUtils.buildCelWithVars(this.expressionValueVars, celTypes);
        this.program = CelUtils.buildProgram(this.expression, cel);
    }

    @Override
    public void compile() throws Exception {
        if (this.valueType == ValueType.Expression) {
            this.expression = this.key + NOT_EQUAL + this.value;
            this.buildCelProgram(null);
        } else if (this.valueType == ValueType.String) {
            String valKey = Constant.BUILTIN_KEY + "STR_001";
            this.expression = this.key + NOT_EQUAL + valKey;
            this.buildCelProgram(Map.of(valKey, SimpleType.STRING));
            this.builtinParams = Map.of(valKey, this.value);
        } else if (this.valueType == ValueType.Boolean) {
            boolean b = Boolean.parseBoolean(this.value);
            if (b) {
                this.expression = this.key + NOT_EQUAL + "true";
            } else {
                this.expression = this.key + NOT_EQUAL + "false";
            }
            this.buildCelProgram(null);
        } else if (this.valueType == ValueType.Number) {
            String valKey = Constant.BUILTIN_KEY + "NUM_001";
            this.expression = this.key + NOT_EQUAL + valKey;
            BigDecimal bd = new BigDecimal(this.value);
            BigDecimal striped = bd.stripTrailingZeros();
            Map<String, CelType> celType;
            if (striped.scale() <= 0) {
                // whole number → long, exact
                celType = Map.of(valKey, SimpleType.INT);
                this.builtinParams = Map.of(valKey, striped.longValueExact());
            } else {
                // decimal → double, IEEE 754 precision
                celType = Map.of(valKey, SimpleType.DOUBLE);
                this.builtinParams = Map.of(valKey, striped.doubleValue());
            }
            this.buildCelProgram(celType);
        } else {
            throw new UnsupportedOperationException("Unsupported ValueType");
        }
    }
}
