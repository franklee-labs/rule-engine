package labs.franklee.engine.logic.impl;

import labs.franklee.engine.logic.base.Condition;
import labs.franklee.engine.logic.base.Relation;
import labs.franklee.engine.logic.base.RelationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EqualConditionTest {

    @Test
    void negate_string_returnsNotEqualCondition() {
        EqualCondition cond = new EqualCondition("status", "active", ValueType.String);
        Condition negated = cond.negate();
        assertInstanceOf(NotEqualCondition.class, negated);
    }

    @Test
    void negate_number_returnsNotEqualCondition() {
        EqualCondition cond = new EqualCondition("age", "18", ValueType.Number);
        assertInstanceOf(NotEqualCondition.class, cond.negate());
    }

    @Test
    void negate_boolean_returnsNotEqualCondition() {
        EqualCondition cond = new EqualCondition("active", "true", ValueType.Boolean);
        assertInstanceOf(NotEqualCondition.class, cond.negate());
    }

    @Test
    void resolve_returnsAndContainingSelf() {
        // Inherited from Condition: wraps this instance in AND(path[this])
        EqualCondition cond = new EqualCondition("x", "1", ValueType.Number);
        Relation result = cond.resolve();
        assertEquals(RelationType.And, result.relation());
        assertEquals(1, result.getPathGroup().paths().size());
        assertSame(cond, result.getPathGroup().paths().get(0).conditions().get(0));
    }
}
