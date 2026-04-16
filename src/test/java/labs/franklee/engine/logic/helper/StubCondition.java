package labs.franklee.engine.logic.helper;

import labs.franklee.engine.logic.base.Condition;

/**
 * A concrete Condition for use in unit tests.
 * Stores a name for assertion purposes; negate() returns a new StubCondition prefixed with "!".
 */
public class StubCondition extends Condition {

    private final String name;

    public StubCondition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Condition negate() {
        return new StubCondition("!" + name);
    }
}
