package labs.franklee.engine.logic.impl;

import labs.franklee.engine.context.Context;
import labs.franklee.engine.logic.base.Condition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RegexConditionTest {

    private static Context ctx(Object... kvs) {
        var m = new java.util.HashMap<String, Object>();
        for (int i = 0; i < kvs.length; i += 2) m.put((String) kvs[i], kvs[i + 1]);
        return new Context(m);
    }

    // ---- validate ----

    @Test
    void validate_validRegex_returnsTrue() {
        assertTrue(new RegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+").validate());
    }

    @Test
    void validate_invalidRegex_returnsFalse() {
        assertFalse(new RegexCondition("email", "[invalid(").validate());
    }

    // ---- negate ----

    @Test
    void negate_returnsNotRegexCondition() throws Exception {
        assertInstanceOf(NotRegexCondition.class,
                new RegexCondition("email", "\\d+").negate());
    }

    @Test
    void negate_sameRegex() throws Exception {
        RegexCondition cond = new RegexCondition("email", "\\d+");
        assertEquals("\\d+", ((NotRegexCondition) cond.negate()).getRegex());
    }

    // ---- double negate ----

    @Test
    void doubleNegate_returnsRegexCondition() throws Exception {
        Condition doubleNegated = new RegexCondition("email", "\\d+").negate().negate();
        assertInstanceOf(RegexCondition.class, doubleNegated);
    }

    // ---- compile + execute: match ----

    @Test
    void match() throws Exception {
        RegexCondition cond = new RegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+");
        cond.compile();
        assertTrue(cond.execute(ctx("email", "user@example.com")));
    }

    @Test
    void noMatch() throws Exception {
        RegexCondition cond = new RegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+");
        cond.compile();
        assertFalse(cond.execute(ctx("email", "not-an-email")));
    }

    @Test
    void digits_matchAndNoMatch() throws Exception {
        RegexCondition cond = new RegexCondition("code", "\\d+");
        cond.compile();
        assertTrue(cond.execute(ctx("code", "12345")));
        assertFalse(cond.execute(ctx("code", "123abc")));
    }

    // ---- non-String value → false ----

    @Test
    void nonStringValue_returnsFalse() throws Exception {
        RegexCondition cond = new RegexCondition("age", "\\d+");
        cond.compile();
        assertFalse(cond.execute(ctx("age", 42L)));
    }

    // ---- missing field → false ----

    @Test
    void missingField_returnsFalse() throws Exception {
        RegexCondition cond = new RegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+");
        cond.compile();
        assertFalse(cond.execute(ctx("other", "user@example.com")));
    }

    // ---- before: user context unmodified ----

    @Test
    void before_userContextUnmodified() throws Exception {
        RegexCondition cond = new RegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+");
        cond.compile();

        Map<String, Object> userParams = Map.of("email", "user@example.com");
        Context ctx = new Context(userParams);
        cond.execute(ctx);

        assertTrue(ctx.getParams().keySet().stream()
                .noneMatch(k -> k.startsWith(Constant.BUILTIN_KEY)));
    }

    // ---- compile not called → throws ----

    @Test
    void evaluate_withoutCompile_throws() {
        RegexCondition cond = new RegexCondition("email", "\\d+");
        assertThrows(Exception.class, () -> cond.execute(ctx("email", "123")));
    }

    // ---- NotRegexCondition ----

    @Test
    void notRegex_matchAndNoMatch() throws Exception {
        NotRegexCondition cond = new NotRegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+");
        cond.compile();
        assertTrue(cond.execute(ctx("email", "not-an-email")));
        assertFalse(cond.execute(ctx("email", "user@example.com")));
    }

    @Test
    void notRegex_nonStringValue_returnsFalse() throws Exception {
        NotRegexCondition cond = new NotRegexCondition("age", "\\d+");
        cond.compile();
        assertFalse(cond.execute(ctx("age", 42L)));
    }
}
