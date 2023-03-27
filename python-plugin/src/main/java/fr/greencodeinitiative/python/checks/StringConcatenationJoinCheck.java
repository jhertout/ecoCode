package fr.greencodeinitiative.python.checks;


import fr.greencodeinitiative.python.checks.helpers.TreeHelper;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.*;

@Rule(
        key = StringConcatenationJoinCheck.RULE_KEY,
        name = StringConcatenationJoinCheck.RULE_NAME,
        description = StringConcatenationJoinCheck.MESSAGE_RULE,
        priority = Priority.MINOR,
        tags = {"bug",
                "eco-design",
                "optimized-api",
                "environment",
                "ecocode"})
public class StringConcatenationJoinCheck extends PythonSubscriptionCheck {

    public static final String RULE_KEY = "EBOT007";

    protected static final String MESSAGE_RULE = "Using `str.join()` to perform a string concatenation is not energy efficient.";

    public static final String RULE_NAME = "String Concatenation Join";

    protected static final String FUNCTION_TO_CHECK = "join";
    protected static final String MODULE_TO_CHECK = "str";

    /**
     * Register CALL_EXPR NODE to check if the function `str.join()` is present
     *
     * @param context The context of the analyser
     */
    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::visitNodeString);
    }

    /**
     * parse the CALL_EXPR NODE to check if a the str.join() function is called
     *
     * @param ctx The context of the analyser
     */
    public void visitNodeString(SubscriptionContext ctx) {
        CallExpression callExpression = (CallExpression) ctx.syntaxNode();
        //Check if the function str.join is called
        if (TreeHelper.isMethodCalled((callExpression), MODULE_TO_CHECK, FUNCTION_TO_CHECK)) {
            if (callExpression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
                QualifiedExpression qE = (QualifiedExpression) callExpression.callee();
                //check if the caller of the join function is a String
                if (qE.qualifier().is(Tree.Kind.STRING_LITERAL)) {
                    StringLiteral strL = (StringLiteral) qE.qualifier();
                    if (!strL.stringElements().isEmpty()) {
                        if (strL.stringElements().get(0).is(Tree.Kind.STRING_ELEMENT)) {
                            //check if the caller of the join function is an Empty String
                            StringElement element = strL.stringElements().get(0);
                            if (element.value().replaceAll("^\"|\"$", "").isEmpty()) {
                                //here we have "".join()
                                ctx.addIssue(callExpression, MESSAGE_RULE);
                            }
                        }
                    }
                }
            }
        } else {
            //if I cannot check the caller type
            // I will not be able to determine it's value and therefore if it is empty
        }
    }
}