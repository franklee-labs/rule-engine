package labs.franklee.engine.logic.impl;

import labs.franklee.engine.context.Context;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CelConditionTest {

    private static Context ctx(Object... kvs) {
        Map<String, Object> m = new java.util.HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            m.put((String) kvs[i], kvs[i + 1]);
        }
        return new Context(m);
    }

    // ---- CelCondition basic evaluation ----

    @Test
    void evaluate_true() {
        CelCondition c = new CelCondition("age > 18");
        assertTrue(c.evaluate(ctx("age", 25L)));
    }

    @Test
    void evaluate_false() {
        CelCondition c = new CelCondition("age > 18");
        assertFalse(c.evaluate(ctx("age", 16L)));
    }

    // ---- negate() returns NegateCelCondition ----

    @Test
    void negate_returnsNegateCelCondition() {
        CelCondition c = new CelCondition("age > 18");
        assertInstanceOf(NegateCelCondition.class, c.negate());
    }

    @Test
    void negate_flipsResult() {
        CelCondition c = new CelCondition("age > 18");
        var negated = c.negate();

        assertFalse(negated.evaluate(ctx("age", 25L)));
        assertTrue (negated.evaluate(ctx("age", 16L)));
    }

    // ---- NegateCelCondition.negate() returns the original ----

    @Test
    void negate_expressionIsWrappedWithBang() {
        CelCondition c = new CelCondition("age > 18");
        NegateCelCondition negated = (NegateCelCondition) c.negate();

        assertEquals("!(age > 18)", negated.getExpression());
    }

    @Test
    void doubleNegate_returnsSameOrigin() {
        CelCondition origin = new CelCondition("age > 18");
        var negated = (NegateCelCondition) origin.negate();

        // negate() of NegateCelCondition must return the original CelCondition instance
        assertSame(origin, negated.negate());
    }

    @Test
    void doubleNegate_sameResultAsOriginal() {
        CelCondition origin = new CelCondition("age > 18");
        var doubleNegated = origin.negate().negate();

        Context pass = ctx("age", 25L);
        Context fail = ctx("age", 16L);

        assertEquals(origin.evaluate(pass), doubleNegated.evaluate(pass));
        assertEquals(origin.evaluate(fail), doubleNegated.evaluate(fail));
    }
}
