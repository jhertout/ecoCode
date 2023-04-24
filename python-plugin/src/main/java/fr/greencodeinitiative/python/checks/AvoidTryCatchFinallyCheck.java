package fr.greencodeinitiative.python.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.TryStatement;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(
        key = AvoidTryCatchFinallyCheck.RULE_KEY,
        name = "Avoid using try-catch statement",
        description = AvoidTryCatchFinallyCheck.DESCRIPTION,
        priority = Priority.MINOR,
        tags = {"bug", "eco-design", "ecocode"})
@DeprecatedRuleKey(repositoryKey = "gci-python", ruleKey = "S34")
public class AvoidTryCatchFinallyCheck extends PythonSubscriptionCheck {

    public static final String RULE_KEY = "EC34";
    public static final String DESCRIPTION = "Avoid the use of try-catch";

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.TRY_STMT, this::visitNode);
    }

    public void visitNode(SubscriptionContext ctx) {
        TryStatement tryStatement = (TryStatement) ctx.syntaxNode();
        ctx.addIssue(tryStatement.tryKeyword(), DESCRIPTION);

    }

}
