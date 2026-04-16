package labs.franklee.engine.logic.impl;

import labs.franklee.engine.logic.base.Condition;
import labs.franklee.engine.logic.base.Relation;
import labs.franklee.engine.logic.base.RelationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotEqualConditionTest {

    @Test
    void negate_string_returnsEqualCondition() {
        NotEqualCondition cond = new NotEqualCondition("status", "active", ValueType.String);
        Condition negated = cond.negate();
        assertInstanceOf(EqualCondition.class, negated);
    }

    @Test
    void negate_number_returnsEqualCondition() {
        NotEqualCondition cond = new NotEqualCondition("age", "18", ValueType.Number);
        assertInstanceOf(EqualCondition.class, cond.negate());
    }

    @Test
    void negate_boolean_returnsEqualCondition() {
        NotEqualCondition cond = new NotEqualCondition("active", "false", ValueType.Boolean);
        assertInstanceOf(EqualCondition.class, cond.negate());
    }

    @Test
    void resolve_returnsAndContainingSelf() {
        NotEqualCondition cond = new NotEqualCondition("x", "1", ValueType.Number);
        Relation result = cond.resolve();
        assertEquals(RelationType.And, result.relation());
        assertEquals(1, result.getPathGroup().paths().size());
        assertSame(cond, result.getPathGroup().paths().get(0).conditions().get(0));
    }
}
