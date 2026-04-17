package labs.franklee.engine.logic.impl;

import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;
import dev.cel.bundle.Cel;
import dev.cel.bundle.CelFactory;
import dev.cel.common.CelAbstractSyntaxTree;
import dev.cel.common.CelFunctionDecl;
import dev.cel.common.CelOverloadDecl;
import dev.cel.common.types.SimpleType;
import dev.cel.runtime.CelFunctionBinding;
import dev.cel.runtime.CelRuntime;
import labs.franklee.engine.context.Context;
import labs.franklee.engine.exceptions.EvalException;
import labs.franklee.engine.logic.base.Condition;

import java.util.Set;

/**
 * Checks whether a String field fully matches a regular expression.
 *
 * <p>Uses RE2J ({@link com.google.re2j.Pattern}) — the same engine as CEL — so behaviour is
 * consistent regardless of which layer evaluates the expression. The pattern is compiled once in
 * {@link #compile()} and captured in a custom CEL function, so no regex compilation happens
 * during rule evaluation.
 *
 * <p>Usage:
 * <pre>
 *   new RegexCondition("email", "[\\w.+-]+@[\\w-]+\\.[\\w.]+")
 *   new RegexCondition("phone", "1[3-9]\\d{9}")
 * </pre>
 *
 * <p>Extension guide – implementing a Condition with a CEL custom function:
 * <ol>
 *   <li>{@link #validate()} – verify the configuration before any compilation work.
 *   <li>{@link #compile()} – pre-compile resources and register them as a custom CEL function via
 *       {@link CelFunctionDecl} + {@link CelFunctionBinding}. The lambda captures the compiled
 *       resource, so evaluation never triggers recompilation.
 *   <li>{@link #before(Context)} – merge builtinParams into the eval map (null when none needed).
 *   <li>{@link #evaluate(Context)} – run the pre-compiled program; identical pattern to all other conditions.
 *   <li>{@link #negate()} – return the logical inverse via {@code condition.build()}.
 * </ol>
 */
public class RegexCondition extends Condition {

    private static final String FUNC_NAME = "regex_matches";
    private static final String OVERLOAD_ID = "regex_matches_string";

    private final String key;
    private final String regex;

    private CelRuntime.Program program;

    public RegexCondition(String key, String regex) {
        super();
        this.setName("RegexCondition");
        this.key = key;
        this.regex = regex;
    }

    @Override
    public boolean validate() {
        try {
            Pattern.compile(this.regex);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    @Override
    public Condition negate() throws Exception {
        Condition condition = new NotRegexCondition(this.key, this.regex);
        condition.build();
        return condition;
    }

    @Override
    public void before(Context context) {
        // no builtin params to inject; still required to initialise evalParams
        context.buildEvalParams(null);
    }

    @Override
    public boolean evaluate(Context context) {
        try {
            Object result = this.program.eval(context.getEvalParam());
            return result instanceof Boolean b && b;
        } catch (Throwable e) {
            throw new EvalException(e);
        }
    }

    @Override
    public void compile() throws Exception {
        Pattern pattern = Pattern.compile(this.regex);

        String expression = FUNC_NAME + "(" + this.key + ")";
        Set<String> varNames = CelUtils.extractTopVarNames(expression);

        var builder = CelFactory.standardCelBuilder()
                .addFunctionDeclarations(
                        CelFunctionDecl.newFunctionDeclaration(
                                FUNC_NAME,
                                CelOverloadDecl.newGlobalOverload(OVERLOAD_ID, SimpleType.BOOL, SimpleType.DYN)))
                .addFunctionBindings(
                        CelFunctionBinding.from(OVERLOAD_ID, Object.class,
                                val -> val instanceof String s ? pattern.matches(s) : Boolean.FALSE));
        varNames.forEach(v -> builder.addVar(v, SimpleType.DYN));

        Cel cel = builder.build();
        CelAbstractSyntaxTree ast = cel.compile(expression).getAst();
        this.program = cel.createProgram(ast);
    }

    String getRegex() {
        return this.regex;
    }
}
