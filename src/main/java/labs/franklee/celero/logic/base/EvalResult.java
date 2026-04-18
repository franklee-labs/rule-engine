package labs.franklee.celero.logic.base;

public final class EvalResult {

    public static final EvalResult TRUE  = new EvalResult(State.TRUE);
    public static final EvalResult FALSE = new EvalResult(State.FALSE);
    public static final EvalResult MISS  = new EvalResult(State.MISS);

    private EvalResult(State state) {
        this.state = state;
    }

    public enum State { TRUE, FALSE, MISS }

    private final State state;

    public boolean isTrue()    { return state == State.TRUE; }
    public boolean isFalse()   { return state == State.FALSE; }
    public boolean isMissing() { return state == State.MISS; }
}