package labs.franklee.celero.logic.impl;

import labs.franklee.celero.context.Context;
import labs.franklee.celero.logic.base.Relation;
import labs.franklee.celero.logic.base.RelationType;
import labs.franklee.celero.logic.base.ValueType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotEqualConditionTest {

    private static Context ctx(Object... kvs) {
        var m = new java.util.HashMap<String, Object>();
        for (int i = 0; i < kvs.length; i += 2) m.put((String) kvs[i], kvs[i + 1]);
        return new Context(m);
    }

    // ---- negate ----

    @Test
    void negate_returnsEqualCondition() throws Exception {
        assertInstanceOf(EqualCondition.class,
                new NotEqualCondition("status", "active", ValueType.String).negate());
        assertInstanceOf(EqualCondition.class,
                new NotEqualCondition("age", "18", ValueType.Number).negate());
        assertInstanceOf(EqualCondition.class,
                new NotEqualCondition("active", "true", ValueType.Boolean).negate());
    }

    // ---- resolve ----

    @Test
    void resolve_returnsAndContainingSelf() {
        NotEqualCondition cond = new NotEqualCondition("x", "1", ValueType.Number);
        Relation result = cond.resolve();
        assertEquals(RelationType.And, result.relation());
        assertEquals(1, result.getPathGroup().paths().size());
        assertSame(cond, result.getPathGroup().paths().get(0).conditions().get(0));
    }

    // ---- compile + execute: String ----

    @Test
    void string_sameValue_returnsFalse() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("role", "admin", ValueType.String);
        cond.compile();
        assertFalse(cond.execute(ctx("role", "admin")));
    }

    @Test
    void string_differentValue_returnsTrue() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("role", "admin", ValueType.String);
        cond.compile();
        assertTrue(cond.execute(ctx("role", "user")));
    }

    @Test
    void string_valueWithSpecialChars_handledByBinding() throws Exception {
        // value contains quotes — safe because it goes through variable binding, not expression concat
        NotEqualCondition cond = new NotEqualCondition("name", "say \"hello\"", ValueType.String);
        cond.compile();
        assertFalse(cond.execute(ctx("name", "say \"hello\"")));
        assertTrue(cond.execute(ctx("name", "say hello")));
    }

    // ---- compile + execute: Boolean ----

    @Test
    void boolean_true_sameValue_returnsFalse() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("active", "true", ValueType.Boolean);
        cond.compile();
        assertFalse(cond.execute(ctx("active", true)));
    }

    @Test
    void boolean_false_sameValue_returnsFalse() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("active", "false", ValueType.Boolean);
        cond.compile();
        assertFalse(cond.execute(ctx("active", false)));
    }

    @Test
    void boolean_differentValue_returnsTrue() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("active", "true", ValueType.Boolean);
        cond.compile();
        assertTrue(cond.execute(ctx("active", false)));
    }

    // ---- compile + execute: Number (integer) ----

    @Test
    void number_integer_sameValue_returnsFalse() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("age", "18", ValueType.Number);
        cond.compile();
        assertFalse(cond.execute(ctx("age", 18L)));
    }

    @Test
    void number_integer_differentValue_returnsTrue() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("age", "18", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("age", 20L)));
    }

    @Test
    void number_integerWithTrailingZeros_treatedAsLong() throws Exception {
        // "18.00" strips to "18" → compiled as long
        NotEqualCondition cond = new NotEqualCondition("age", "18.00", ValueType.Number);
        cond.compile();
        assertFalse(cond.execute(ctx("age", 18L)));
        assertTrue(cond.execute(ctx("age", 19L)));
    }

    // ---- compile + execute: Number (decimal) ----

    @Test
    void number_decimal_sameValue_returnsFalse() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("score", "99.5", ValueType.Number);
        cond.compile();
        assertFalse(cond.execute(ctx("score", 99.5)));
    }

    @Test
    void number_decimal_differentValue_returnsTrue() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("score", "99.5", ValueType.Number);
        cond.compile();
        assertTrue(cond.execute(ctx("score", 99.0)));
    }

    // ---- compile + execute: Expression ----

    @Test
    void expression_crossFieldInequality() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("a", "b", ValueType.Expression);
        cond.compile();
        assertFalse(cond.execute(ctx("a", 10L, "b", 10L)));
        assertTrue(cond.execute(ctx("a", 10L, "b", 20L)));
    }

    // ---- before: builtin params not leaked into user context ----

    @Test
    void before_userContextUnmodified() throws Exception {
        NotEqualCondition cond = new NotEqualCondition("role", "admin", ValueType.String);
        cond.compile();

        Map<String, Object> userParams = Map.of("role", "user");
        Context ctx = new Context(userParams);
        cond.execute(ctx);

        assertTrue(ctx.getParams().keySet().stream()
                .noneMatch(k -> k.startsWith(Constant.BUILTIN_KEY)));
    }

    // ---- compile not called → program is null → throws ----

    @Test
    void evaluate_withoutCompile_throws() {
        NotEqualCondition cond = new NotEqualCondition("role", "admin", ValueType.String);
        assertThrows(Exception.class, () -> cond.execute(ctx("role", "admin")));
    }

    // ---- validate ----

    @Test
    void validate_number_validInteger_returnsTrue() {
        assertTrue(new NotEqualCondition("age", "18", ValueType.Number).validate());
    }

    @Test
    void validate_number_validDecimal_returnsTrue() {
        assertTrue(new NotEqualCondition("score", "99.5", ValueType.Number).validate());
    }

    @Test
    void validate_number_invalidString_returnsFalse() {
        assertFalse(new NotEqualCondition("age", "abc", ValueType.Number).validate());
    }

    @Test
    void validate_string_returnsTrue() {
        assertTrue(new NotEqualCondition("role", "admin", ValueType.String).validate());
    }

    @Test
    void validate_boolean_returnsTrue() {
        assertTrue(new NotEqualCondition("active", "true", ValueType.Boolean).validate());
    }

    @Test
    void validate_expression_returnsTrue() {
        assertTrue(new NotEqualCondition("a", "b", ValueType.Expression).validate());
    }
}
