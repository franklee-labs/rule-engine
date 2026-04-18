package labs.franklee.celero.logic.impl;

import labs.franklee.celero.context.Context;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CelConditionTest {

    private static Context ctx(Object... kvs) {
        Map<String, Object> m = new java.util.HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            m.put((String) kvs[i], kvs[i + 1]);
        }
        return Context.Builder.createBuilder(m).build();
    }

    // ---- CelCondition basic evaluation ----

    @Test
    void execute_true() throws Exception {
        CelCondition c = new CelCondition("age > 18");
        c.compile();
        assertTrue(c.execute(ctx("age", 25L)).isTrue());
    }

    @Test
    void execute_false() throws Exception {
        CelCondition c = new CelCondition("age > 18");
        c.compile();
        assertTrue(c.execute(ctx("age", 16L)).isFalse());
    }

    // ---- negate() returns NegateCelCondition ----

    @Test
    void negate_returnsNegateCelCondition() throws Exception {
        CelCondition c = new CelCondition("age > 18");
        assertInstanceOf(NegateCelCondition.class, c.negate());
    }

    @Test
    void negate_flipsResult() throws Exception {
        CelCondition c = new CelCondition("age > 18");
        var negated = c.negate();

        assertTrue(negated.execute(ctx("age", 25L)).isFalse());
        assertTrue(negated.execute(ctx("age", 16L)).isTrue());
    }

    // ---- NegateCelCondition.negate() returns the original ----

    @Test
    void negate_expressionIsWrappedWithBang() throws Exception {
        CelCondition c = new CelCondition("age > 18");
        NegateCelCondition negated = (NegateCelCondition) c.negate();

        assertEquals("!(age > 18)", negated.getExpression());
    }

    @Test
    void doubleNegate_returnsSameOrigin() throws Exception {
        CelCondition origin = new CelCondition("age > 18");
        var negated = (NegateCelCondition) origin.negate();

        // negate() of NegateCelCondition must return the original CelCondition instance
        assertSame(origin, negated.negate());
    }

    @Test
    void doubleNegate_sameResultAsOriginal() throws Exception {
        CelCondition origin = new CelCondition("age > 18");
        origin.compile();
        var doubleNegated = origin.negate().negate();

        Context pass = ctx("age", 25L);
        Context fail = ctx("age", 16L);

        assertEquals(origin.execute(pass).isTrue(), doubleNegated.execute(pass).isTrue());
        assertEquals(origin.execute(fail).isTrue(), doubleNegated.execute(fail).isTrue());
    }
}
