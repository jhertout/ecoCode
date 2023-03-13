package fr.greencodeinitiative.python.checks;

import fr.greencodeinitiative.python.checks.helpers.TreeHelper;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;

import java.util.*;

@Rule(
        key = "EBOT002",
        name = "Developpement",
        description = AvoidCopyLibDeepcopy.MESSAGE_RULE,
        priority = Priority.MAJOR,
        tags = {"bug"})
public class AvoidCopyLibDeepcopy extends PythonSubscriptionCheck {

    private static final List<String> COPY_LIB = Arrays.asList("copy");

    protected static final String MESSAGE_RULE = "Avoid using Lib/copy.deepcopy(x)";

    protected static final String FUNCTION_TO_CHECK = "deepcopy";
    protected static final String MODULE_TO_CHECK = "copy";

    private boolean isUsingModule = false;

    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FILE_INPUT, this::visitFile);
        context.registerSyntaxNodeConsumer(Tree.Kind.CALL_EXPR, this::checkCallExpression);
    }

    private void visitFile(SubscriptionContext ctx) {
        FileInput tree = (FileInput) ctx.syntaxNode();
        SymbolsFromImport visitor = new SymbolsFromImport();
        tree.accept(visitor);
        visitor.symbols.stream()
                .filter(Objects::nonNull)
                .map(Symbol::fullyQualifiedName)
                .filter(Objects::nonNull)
                .forEach(qualifiedName -> {
                    if (COPY_LIB.contains(qualifiedName)) {
                        isUsingModule = true;
                    }
                });
    }

    private static class SymbolsFromImport extends BaseTreeVisitor {
        private final Set<Symbol> symbols = new HashSet<>();

        @Override
        public void visitAliasedName(AliasedName aliasedName) {
            List<Name> names = aliasedName.dottedName().names();
            symbols.add(names.get(names.size() - 1).symbol());
        }
    }

    private void checkCallExpression(SubscriptionContext context) {
        CallExpression expression = (CallExpression) context.syntaxNode();
        if (isUsingModule && TreeHelper.isMethodCalled(expression, MODULE_TO_CHECK, FUNCTION_TO_CHECK, "list")) {
            context.addIssue(expression, MESSAGE_RULE);
        }
    }
}