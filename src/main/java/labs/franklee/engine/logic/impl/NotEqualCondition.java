package labs.franklee.engine.logic.impl;

import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Condition;

import java.math.BigDecimal;

public class NotEqualCondition extends Condition {

    private final String key;

    private final ValueType valueType;

    private final String rawValue;

    private String stringValue = null;

    private BigDecimal numberValue = null;

    private Boolean boolValue = null;

    public NotEqualCondition(String key, String value, ValueType valueType) {
        super();
        this.key = key;
        this.rawValue = value;
        this.valueType = valueType;
        if (this.valueType == ValueType.String) {
            this.stringValue = value;
        } else if (this.valueType == ValueType.Number) {
            this.numberValue = new BigDecimal(value);
        } else {
            this.boolValue = Boolean.valueOf(value);
        }
    }

    @Override
    public Condition negate() {
        return new EqualCondition(this.key, this.rawValue, this.valueType);
    }

    @Override
    public boolean evaluate(Context context) {
        return false;
    }
}
