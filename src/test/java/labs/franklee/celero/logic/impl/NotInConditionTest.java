package labs.franklee.celero.logic.impl;

import labs.franklee.celero.context.Context;
import labs.franklee.celero.logic.base.Relation;
import labs.franklee.celero.logic.base.RelationType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotInConditionTest {

    private static Context ctx(Object... kvs) {
        var m = new java.util.HashMap<String, Object>();
        for (int i = 0; i < kvs.length; i += 2) m.put((String) kvs[i], kvs[i + 1]);
        return new Context(m);
    }

    // ---- negate ----

    @Test
    void negate_returnsInCondition() throws Exception {
        assertInstanceOf(InCondition.class,
                new NotInCondition("role", List.of("admin")).negate());
    }

    // ---- resolve ----

    @Test
    void resolve_returnsAndContainingSelf() {
        NotInCondition cond = new NotInCondition("role", List.of("admin"));
        Relation result = cond.resolve();
        assertEquals(RelationType.And, result.relation());
        assertSame(cond, result.getPathGroup().paths().get(0).conditions().get(0));
    }

    // ---- validate ----

    @Test
    void validate_nullList_returnsFalse() {
        assertFalse(new NotInCondition("role", null).validate());
    }

    @Test
    void validate_emptyList_returnsFalse() {
        assertFalse(new NotInCondition("role", List.of()).validate());
    }

    @Test
    void validate_nonEmptyList_returnsTrue() {
        assertTrue(new NotInCondition("role", List.of("admin")).validate());
    }

    // ---- compile + execute: String ----

    @Test
    void string_inList_returnsFalse() throws Exception {
        NotInCondition cond = new NotInCondition("role", List.of("admin", "ops"));
        cond.compile();
        assertFalse(cond.execute(ctx("role", "admin")));
        assertFalse(cond.execute(ctx("role", "ops")));
    }

    @Test
    void string_notInList_returnsTrue() throws Exception {
        NotInCondition cond = new NotInCondition("role", List.of("admin", "ops"));
        cond.compile();
        assertTrue(cond.execute(ctx("role", "user")));
    }

    @Test
    void string_singleElement_notMatch_returnsTrue() throws Exception {
        NotInCondition cond = new NotInCondition("role", List.of("admin"));
        cond.compile();
        assertTrue(cond.execute(ctx("role", "ops")));
        assertFalse(cond.execute(ctx("role", "admin")));
    }

    // ---- compile + execute: Number (Long) ----

    @Test
    void long_inList_returnsFalse() throws Exception {
        NotInCondition cond = new NotInCondition("age", List.of(18L, 25L, 30L));
        cond.compile();
        assertFalse(cond.execute(ctx("age", 18L)));
    }

    @Test
    void long_notInList_returnsTrue() throws Exception {
        NotInCondition cond = new NotInCondition("age", List.of(18L, 25L, 30L));
        cond.compile();
        assertTrue(cond.execute(ctx("age", 20L)));
    }

    // ---- compile + execute: Number (Double) ----

    @Test
    void double_inList_returnsFalse() throws Exception {
        NotInCondition cond = new NotInCondition("score", List.of(99.5, 88.0));
        cond.compile();
        assertFalse(cond.execute(ctx("score", 99.5)));
    }

    @Test
    void double_notInList_returnsTrue() throws Exception {
        NotInCondition cond = new NotInCondition("score", List.of(99.5, 88.0));
        cond.compile();
        assertTrue(cond.execute(ctx("score", 70.0)));
    }

    // ---- compile + execute: Boolean ----

    @Test
    void boolean_inList_returnsFalse() throws Exception {
        NotInCondition cond = new NotInCondition("flag", List.of(true));
        cond.compile();
        assertFalse(cond.execute(ctx("flag", true)));
    }

    @Test
    void boolean_notInList_returnsTrue() throws Exception {
        NotInCondition cond = new NotInCondition("flag", List.of(true));
        cond.compile();
        assertTrue(cond.execute(ctx("flag", false)));
    }

    // ---- compile + execute: mixed types ----

    @Test
    void mixed_inList_returnsFalse() throws Exception {
        NotInCondition cond = new NotInCondition("val", List.of("admin", 18L, 99.5, true));
        cond.compile();
        assertFalse(cond.execute(ctx("val", "admin")));
        assertFalse(cond.execute(ctx("val", 18L)));
        assertFalse(cond.execute(ctx("val", 99.5)));
        assertFalse(cond.execute(ctx("val", true)));
    }

    @Test
    void mixed_notInList_returnsTrue() throws Exception {
        NotInCondition cond = new NotInCondition("val", List.of("admin", 18L, 99.5, true));
        cond.compile();
        assertTrue(cond.execute(ctx("val", "unknown")));
    }

    // ---- cross-type: Long field vs Double list, Double field vs Long list ----

    @Test
    void longField_againstDoubleList_sameNumericValue_returnsFalse() throws Exception {
        // CEL performs numeric coercion between int and double: 18L == 18.0 → in list → not-in is false
        NotInCondition cond = new NotInCondition("age", List.of(18.0, 25.0));
        cond.compile();
        assertFalse(cond.execute(ctx("age", 18L)));
    }

    @Test
    void doubleField_againstLongList_sameNumericValue_returnsFalse() throws Exception {
        // CEL performs numeric coercion between int and double: 18.0 == 18L → in list → not-in is false
        NotInCondition cond = new NotInCondition("score", List.of(18L, 25L));
        cond.compile();
        assertFalse(cond.execute(ctx("score", 18.0)));
    }

    // ---- BigDecimal normalization (deserialization scenario) ----

    @Test
    void bigDecimal_integer_normalizedToLong() throws Exception {
        NotInCondition cond = new NotInCondition("age", List.of(new BigDecimal("18.00"), new BigDecimal("25")));
        cond.compile();
        assertFalse(cond.execute(ctx("age", 18L)));
        assertTrue(cond.execute(ctx("age", 20L)));
    }

    @Test
    void bigDecimal_decimal_normalizedToDouble() throws Exception {
        NotInCondition cond = new NotInCondition("score", List.of(new BigDecimal("99.50")));
        cond.compile();
        assertFalse(cond.execute(ctx("score", 99.5)));
        assertTrue(cond.execute(ctx("score", 70.0)));
    }

    @Test
    void integer_normalizedToLong() throws Exception {
        NotInCondition cond = new NotInCondition("age", List.of((Object) Integer.valueOf(18)));
        cond.compile();
        assertFalse(cond.execute(ctx("age", 18L)));
        assertTrue(cond.execute(ctx("age", 20L)));
    }

    // ---- before: builtin params not leaked into user context ----

    @Test
    void before_userContextUnmodified() throws Exception {
        NotInCondition cond = new NotInCondition("role", List.of("admin", "ops"));
        cond.compile();

        Map<String, Object> userParams = Map.of("role", "user");
        Context ctx = new Context(userParams);
        cond.execute(ctx);

        assertTrue(ctx.getParams().keySet().stream()
                .noneMatch(k -> k.startsWith(Constant.BUILTIN_KEY)));
    }

    // ---- compile not called → throws ----

    @Test
    void evaluate_withoutCompile_throws() {
        NotInCondition cond = new NotInCondition("role", List.of("admin"));
        assertThrows(Exception.class, () -> cond.execute(ctx("role", "admin")));
    }
}
