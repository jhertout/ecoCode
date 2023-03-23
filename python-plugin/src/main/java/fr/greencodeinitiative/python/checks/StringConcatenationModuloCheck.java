package fr.greencodeinitiative.python.checks;


import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Usage;
import org.sonar.plugins.python.api.tree.*;

@Rule(
        key = StringConcatenationModuloCheck.RULE_KEY,
        name = StringConcatenationModuloCheck.RULE_NAME,
        description = StringConcatenationModuloCheck.MESSAGE_RULE,
        priority = Priority.MAJOR,
        tags = {"bug",
                "eco-design",
                "optimized-api",
                "environment",
                "ecocode"})
public class StringConcatenationModuloCheck extends PythonSubscriptionCheck {

    public static final String RULE_KEY = "EBOT006";

    protected static final String MESSAGE_RULE = "Using modulo concatenation to perform a string concatenation is not energy efficient.";

    public static final String RULE_NAME = "String Concatenation Modulo";

    /**
     * Register EXPRESSION_STMT NODE to check if a modulo expression is present
     *
     * @param context The context of the analyser
     *
     */
    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.EXPRESSION_STMT, this::visitNodeString);
    }

    /**
     * Check the expression, is it a modulo
     * if it is a modulo expression
     * Check that the left operand is a string
     * Check that the right operand is a Tuple OR a String
     *
     * @param ctx The context that subscribes to the EXPRESSION_STMT node
     */
    public void visitNodeString(SubscriptionContext ctx) {
        ExpressionStatement statmnt = (ExpressionStatement) ctx.syntaxNode();
        if (!statmnt.expressions().isEmpty()) {
            if (statmnt.expressions().get(0).is(Tree.Kind.MODULO)) {
                BinaryExpression moduloExpression = (BinaryExpression) statmnt.expressions().get(0);
                if (moduloExpression.leftOperand().type().mustBeOrExtend("str")) {
                    //module is called on a string
                    if (moduloExpression.rightOperand().is(Tree.Kind.TUPLE)) {
                        //Python String Interpolation with the Percent (%) Operator
                        ctx.addIssue(moduloExpression, MESSAGE_RULE);
                    } else if (moduloExpression.rightOperand().type().mustBeOrExtend("str")) {
                        ctx.addIssue(moduloExpression, MESSAGE_RULE);
                    } else if (moduloExpression.rightOperand().is(Tree.Kind.STRING_LITERAL)) {
                        ctx.addIssue(moduloExpression, MESSAGE_RULE);
                    } else {
                        //check if the method caller assignment is String
                        checkCallerType(ctx, moduloExpression.rightOperand(),moduloExpression);
                    }
                }
            }
        }
    }

    /**
     * Check the Right Operand
     * If it is a variable
     * Check all the usages of the variable to check the assignment
     *
     * @param ctx The context that subscribes to the EXPRESSION_STMT node
     * @param callExpression the Righ Operand of the Binary expression
     * @param moduloExpr the modulo Expression to report
     */
    private void checkCallerType(SubscriptionContext ctx, Expression callExpression,Expression moduloExpr) {
        if (callExpression.is(Tree.Kind.NAME)) {
            Name nameExpr = (Name) callExpression;
            for (Usage usage : nameExpr.symbol().usages()) {
                checkTypeRecursively(ctx, moduloExpr, usage.tree());
            }
        }
    }

    /**
     * Check all the usages of the variable
     * If an usage is an Assignment
     * And the assignedValue is a String
     * report an Issue on the ModuloExpr tree
     *
     * @param ctx The context that subscribes to the EXPRESSION_STMT node
     * @param expression the modulo expression to report
     * @param tree the Righ Operand of the Binary expression
     */
    private void checkTypeRecursively(SubscriptionContext ctx, Tree expression, Tree tree) {
        if (tree.is(Tree.Kind.ASSIGNMENT_STMT)) {
            AssignmentStatement assignmentStatement = (AssignmentStatement) tree;
            if (assignmentStatement.assignedValue().type().mustBeOrExtend("str")) {
                ctx.addIssue(expression, MESSAGE_RULE);
            }
        } else {
            if (tree.parent() != null) {
                checkTypeRecursively(ctx, expression, tree.parent());
            }
        }

    }
}