package fr.greencodeinitiative.python.checks;


import fr.greencodeinitiative.python.checks.helpers.TreeHelper;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Usage;
import org.sonar.plugins.python.api.tree.*;

@Rule(
        key = StringConcatenationFormatCheck.RULE_KEY,
        name = StringConcatenationFormatCheck.RULE_NAME,
        description = StringConcatenationFormatCheck.MESSAGE_RULE,
        priority = Priority.MINOR,
        tags = {"bug",
                "eco-design",
                "optimized-api",
                "environment",
                "ecocode"})
public class StringConcatenationFormatCheck extends PythonSubscriptionCheck {

    public static final String RULE_KEY = "EBOT005";

    protected static final String MESSAGE_RULE = "Using `str.format()` to perform a string concatenation is not energy efficient.";

    public static final String RULE_NAME = "String Concatenation Format";

    protected static final String FUNCTION_TO_CHECK = "format";
    protected static final String MODULE_TO_CHECK = "str";

    /**
     * Register CALL_EXPR NODE to check if the function `str.format()` is present
     *
     * @param context The context of the analyser
     */
    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::visitNodeString);
    }

    /**
     * parse the CALL_EXPR NODE to check if a the str.format() function is called
     *
     * @param ctx The context of the analyser
     */
    public void visitNodeString(SubscriptionContext ctx) {
        CallExpression callExpression = (CallExpression) ctx.syntaxNode();
        if (TreeHelper.isMethodCalled((callExpression), MODULE_TO_CHECK, FUNCTION_TO_CHECK)) {
            if (callExpression.callee().is(Tree.Kind.NAME)) {
                checkVariableAssignmentType(ctx, callExpression, (QualifiedExpression) callExpression.callee());
            } else if (callExpression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
                //Check Parent recursively to check if the expression return a string
                QualifiedExpression qE = (QualifiedExpression) callExpression.callee();
                if (qE.type().mustBeOrExtend("str")) {
                    ctx.addIssue(callExpression, MESSAGE_RULE);
                } else {
                    if (qE.qualifier().is(Tree.Kind.NAME)) {
                        checkVariableAssignmentType(ctx, callExpression, qE);
                    } else if (qE.qualifier().is(Tree.Kind.STRING_LITERAL)) {
                        ctx.addIssue(callExpression, MESSAGE_RULE);
                    }
                }
            }
        } else {
            //if I cannot check the caller type
            checkCallerType(ctx, callExpression);
        }

    }

    /**
     * Check the type of the assigned value af the caller of the method
     *
     * @param ctx            the subscription context of the analyser
     * @param callExpression the expression to report
     * @param qE             the caller of the method
     */
    private void checkVariableAssignmentType(SubscriptionContext ctx, CallExpression callExpression, QualifiedExpression qE) {
        Name qualifier = (Name) qE.qualifier();
        if (qualifier.isVariable()) {
            for (Usage usage : qualifier.symbol().usages()) {
                if (usage.kind().equals(Usage.Kind.ASSIGNMENT_LHS)) {
                    //here qualifier is the caller

                    //this methode needs a callExpression To Report (the parent method)
                    checkParentTypeRecursively(ctx, callExpression, usage.tree());
                }
            }
        }
    }

    /**
     * If the caller type is not defined
     * check if it is a String
     * if it is a variable
     * check if the assigned type is a string
     *
     * @param ctx            the subscription context of the parser
     * @param callExpression the expression to report
     */
    private void checkCallerType(SubscriptionContext ctx, CallExpression callExpression) {
        if (TreeHelper.getMethodName(callExpression).equals(FUNCTION_TO_CHECK)) {
            //here the method name is called, we need to check if a string is the caller
            if (callExpression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
                QualifiedExpression qE = (QualifiedExpression) callExpression.callee();
                if (qE.qualifier().type().mustBeOrExtend("str")) {
                    ctx.addIssue(callExpression, MESSAGE_RULE);
                } else if (qE.qualifier().is(Tree.Kind.NAME)) {
                    checkVariableAssignmentType(ctx, callExpression, qE);
                }
            }
        }
    }

    /**
     * Recusive method to check if an assignment of the varibale is a String
     *
     * @param ctx        the subscription context of the parser
     * @param expression the expression to report
     * @param tree       the usage of the caller variable
     */
    private void checkParentTypeRecursively(SubscriptionContext ctx, Tree expression, Tree tree) {
        if (tree.is(Tree.Kind.ASSIGNMENT_STMT)) {
            AssignmentStatement assignmentStatement = (AssignmentStatement) tree;
            if (assignmentStatement.assignedValue().type().mustBeOrExtend("str")) {
                ctx.addIssue(expression, MESSAGE_RULE);
            }
        } else {
            if (tree.parent() != null) {
                checkParentTypeRecursively(ctx, expression, tree.parent());
            }
        }

    }
}