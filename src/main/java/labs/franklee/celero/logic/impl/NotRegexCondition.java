package labs.franklee.celero.logic.impl;

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
import labs.franklee.celero.context.Context;
import labs.franklee.celero.exceptions.MissingParameterException;
import labs.franklee.celero.logic.base.Condition;

import java.util.Set;

/**
 * Logical inverse of {@link RegexCondition}: passes when the field does NOT match the pattern.
 */
public class NotRegexCondition extends Condition {

    private static final String FUNC_NAME = "regex_not_matches";
    private static final String OVERLOAD_ID = "regex_not_matches_string";

    private final String key;
    private final String regex;

    private CelRuntime.Program program;

    public NotRegexCondition(String key, String regex) {
        super();
        this.setName("NotRegexCondition");
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
        Condition condition = new RegexCondition(this.key, this.regex);
        condition.build();
        return condition;
    }

    @Override
    public void before(Context context) {
        context.buildEvalParams(null);
    }

    @Override
    public boolean evaluate(Context context) throws MissingParameterException {
        return this.celEvaluate(this.program, context);
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
                                // non-String is treated as a type mismatch → false, same as RegexCondition
                                val -> val instanceof String s ? !pattern.matches(s) : Boolean.FALSE));
        varNames.forEach(v -> builder.addVar(v, SimpleType.DYN));

        Cel cel = builder.build();
        CelAbstractSyntaxTree ast = cel.compile(expression).getAst();
        this.program = cel.createProgram(ast);
    }

    String getRegex() {
        return this.regex;
    }
}
