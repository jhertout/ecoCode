package fr.greencodeinitiative.python.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.ComprehensionExpression;
import org.sonar.plugins.python.api.tree.Tree;

@Rule(
        key = ListComprehension.RULE_KEY,
        name = ListComprehension.RULE_NAME,
        description = ListComprehension.MESSAGE_RULE,
        priority = Priority.MAJOR,
        tags = {"bug",
                "eco-design",
                "optimized-api",
                "environment",
                "ecocode"})

public class ListComprehension extends PythonSubscriptionCheck {

    protected static final String MESSAGE_RULE = "Using a list comprehension is not energy efficient.";

    public static final String RULE_KEY = "EBOT004";

    public static final String RULE_NAME = "List Comprehension";


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