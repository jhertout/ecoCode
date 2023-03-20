package fr.greencodeinitiative.python.checks;

import fr.greencodeinitiative.python.checks.helpers.TreeHelper;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Usage;
import org.sonar.plugins.python.api.tree.*;

@Rule(
        key = ListAppendInLoop.RULE_KEY,
        name = ListAppendInLoop.RULE_NAME,
        description = ListAppendInLoop.MESSAGE_RULE,
        priority = Priority.MAJOR,
        tags = {"bug",
                "eco-design",
                "optimized-api",
                "environment",
                "ecocode"})
public class ListAppendInLoop extends PythonSubscriptionCheck {

    public static final String RULE_KEY = "EBOT003";

    public static final String RULE_NAME = "List Append In Loop";

    protected static final String MESSAGE_RULE = "Using `list.append(x)` within a loop is not energy efficient.";

    protected static final String FUNCTION_TO_CHECK = "append";

    /**
     * Register CALL_EXPR NODE to check if the function is called
     *
     * @param context The context of the analyser
     */
    @Override
    public void initialize(Context context) {
        // Parse the CALL_EXPR Tree to access the called functions of the file
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkExpression);
    }

    /**
     * Check the Expressions tree
     * If the method called is append()
     * And the caller is a list
     * report an issue
     *
     * @param subscriptionContext The context that subscribes to the CALL_EXPR node
     */
    private void checkExpression(SubscriptionContext subscriptionContext) {
        CallExpression callExpression = (CallExpression) subscriptionContext.syntaxNode();
        if (TreeHelper.getMethodName(callExpression).equals(FUNCTION_TO_CHECK)) {

            if (callExpression.callee().is(Tree.Kind.NAME)) {
                Name name = ((Name) callExpression.callee());
                if (name.isVariable()) {
                    for (Usage usage : name.symbol().usages()) {
                        if (usage.kind().equals(Usage.Kind.ASSIGNMENT_LHS)) {
                            //Check Parent recursively to know if the Variable is a list
                            checkParentTypeRecursively(subscriptionContext, callExpression, callExpression, usage.tree());
                        }
                    }
                }
                //Check the argument, is it an Expression ?
            } else if (callExpression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
                //Check Parent recursively to check if the expression return a list
                //callExpression --> newList.append()
                QualifiedExpression qE = (QualifiedExpression) callExpression.callee();
                if (qE.qualifier().is(Tree.Kind.NAME)) {
                    Name qualifier = (Name) qE.qualifier();
                    if (qualifier.symbol().usages().get(0).kind().equals(Usage.Kind.ASSIGNMENT_LHS)) {
                        //here qualifier is the caller
                        //qualifier.symbol().usages().get(0) is the first usage the assignment

                        //this methode needs a callExpression To Report (the parent method)
                        checkParentTypeRecursively(subscriptionContext, callExpression, callExpression, qualifier.symbol().usages().get(0).tree());
                    }
                }
            }
        }
    }

    /**
     * Recursive method checking parent node, if a parent is a loop it means that the function is call within a Loop Block
     *
     * @param ctx        The context that subscribes to the CALL_EXPR node
     * @param expression the expression that needs to be reported
     * @param tree       the tree containing the expression
     */
    private void isCallInALoop(SubscriptionContext ctx, Tree expression, Tree tree) {
        if (tree.parent() != null && (tree.parent().is(Tree.Kind.WHILE_STMT) || tree.parent().is(Tree.Kind.FOR_STMT))) {
            ctx.addIssue(expression, MESSAGE_RULE);
        } else {
            if (tree.parent() != null) {
                isCallInALoop(ctx, expression, tree.parent());
            }
        }
    }

    /**
     * Recursive method checking function Caller if the caller is a list continue he analyse
     *
     * @param ctx        The context that subscribes to the CALL_EXPR node
     * @param expression the expression that needs to be reported
     * @param tree       the tree containing the caller
     */
    private void checkParentTypeRecursively(SubscriptionContext ctx, Tree expression, Tree initialTreeToParse, Tree tree) {
        if (tree.parent().is(Tree.Kind.ASSIGNMENT_STMT)) {
            AssignmentStatement assignmentStatement = (AssignmentStatement) tree.parent();
            if (assignmentStatement.assignedValue().type().mustBeOrExtend("list")) {
                isCallInALoop(ctx, expression, initialTreeToParse);
            }
        } else {
            checkParentTypeRecursively(ctx, expression, initialTreeToParse, tree.parent());
        }

    }
}