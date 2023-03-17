package fr.greencodeinitiative.python.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.ComprehensionExpression;
import org.sonar.plugins.python.api.tree.Tree;

@Rule(
        key = "EBOT004",
        name = "Developpement",
        description = ListComprehension.MESSAGE_RULE,
        priority = Priority.MAJOR,
        tags = {"bug"})
public class ListComprehension extends PythonSubscriptionCheck {

    protected static final String MESSAGE_RULE = "Using a list comprehension not energy efficient.";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.LIST_COMPREHENSION, this::visitListComp);
    }

    private void visitListComp(SubscriptionContext subscriptionContext) {
        ComprehensionExpression comprehensionExpression = (ComprehensionExpression) subscriptionContext.syntaxNode();
        if (comprehensionExpression.getKind().equals(Tree.Kind.LIST_COMPREHENSION)) {
            subscriptionContext.addIssue(comprehensionExpression, MESSAGE_RULE);
        }
    }
}