package labs.franklee.engine.logic.impl;

import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Relation;
import labs.franklee.engine.logic.base.RelationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LessThanOrEqualConditionTest {

    private static Context ctx(Object... kvs) {
        var m = new java.util.HashMap<String, Object>();
        for (int i = 0; i < kvs.length; i += 2) m.put((String) kvs[i], kvs[i + 1]);
        return new Context(m);
    }

    // ---- negate ----

    @Test
    void negate_returnsGreaterThanCondition() {
        assertInstanceOf(GreaterThanCondition.class,
                new LessThanOrEqualCondition("age", "18", ValueType.Number).negate());
    }

    // ---- resolve ----

    @Test
    void resolve_returnsAndContainingSelf() {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("x", "10", ValueType.Number);
        Relation result = cond.resolve();
        assertEquals(RelationType.And, result.relation());
        assertEquals(1, result.getPathGroup().paths().size());
        assertSame(cond, result.getPathGroup().paths().get(0).conditions().get(0));
    }

    // ---- compile + execute: Number (integer) ----

    @Test
    void number_integer_less_returnsTrue() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("age", "18", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("age", 17L)));
    }

    @Test
    void number_integer_equal_returnsTrue() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("age", "18", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("age", 18L)));
    }

    @Test
    void number_integer_greater_returnsFalse() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("age", "18", ValueType.Number);
        cond.compile();
        assertFalse(cond.execute(ctx("age", 19L)));
    }

    @Test
    void number_integerWithTrailingZeros_treatedAsLong() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("age", "18.00", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("age", 18L)));
        assertFalse(cond.execute(ctx("age", 19L)));
    }

    // ---- compile + execute: Number (decimal) ----

    @Test
    void number_decimal_less_returnsTrue() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("score", "99.5", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("score", 99.0)));
    }

    @Test
    void number_decimal_equal_returnsTrue() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("score", "99.5", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("score", 99.5)));
    }

    @Test
    void number_decimal_greater_returnsFalse() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("score", "99.5", ValueType.Number);
        cond.compile();
        assertFalse(cond.execute(ctx("score", 100.0)));
    }

    // ---- compile + execute: Expression ----

    @Test
    void expression_crossField() throws Exception {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("a", "b", ValueType.Expression);
        cond.compile();
        assertTrue(cond.execute(ctx("a", 5L, "b", 10L)));
        assertTrue(cond.execute(ctx("a", 10L, "b", 10L)));
        assertFalse(cond.execute(ctx("a", 20L, "b", 10L)));
    }

    // ---- unsupported type ----

    @Test
    void compile_unsupportedType_throws() {
        assertThrows(UnsupportedOperationException.class,
                () -> new LessThanOrEqualCondition("role", "admin", ValueType.String).compile());
        assertThrows(UnsupportedOperationException.class,
                () -> new LessThanOrEqualCondition("active", "true", ValueType.Boolean).compile());
    }

    // ---- compile not called → throws ----

    @Test
    void evaluate_withoutCompile_throws() {
        LessThanOrEqualCondition cond = new LessThanOrEqualCondition("age", "18", ValueType.Number);
        assertThrows(Exception.class, () -> cond.execute(ctx("age", 17L)));
    }

    // ---- validate ----

    @Test
    void validate_number_validInteger_returnsTrue() {
        assertTrue(new LessThanOrEqualCondition("age", "18", ValueType.Number).validate());
    }

    @Test
    void validate_number_validDecimal_returnsTrue() {
        assertTrue(new LessThanOrEqualCondition("score", "99.5", ValueType.Number).validate());
    }

    @Test
    void validate_number_invalidString_returnsFalse() {
        assertFalse(new LessThanOrEqualCondition("age", "abc", ValueType.Number).validate());
    }

    @Test
    void validate_expression_returnsTrue() {
        assertTrue(new LessThanOrEqualCondition("a", "b", ValueType.Expression).validate());
    }
}
