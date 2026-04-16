package labs.franklee.engine.logic.helper;

import labs.franklee.engine.logic.base.Node;
import labs.franklee.engine.logic.base.Relation;

/**
 * A Node that intentionally does NOT implement Negatable.
 * Used to cover the defensive "child instanceof Negatable" false-branch in AND/OR/NOT.
 */
public class NonNegatableNode extends Node {

    @Override
    public Relation resolve() {
        return null;
    }
}
