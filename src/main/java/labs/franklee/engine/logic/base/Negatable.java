package labs.franklee.engine.logic.base;

public interface Negatable<T extends Node> {

    T negate() throws Exception;
}
