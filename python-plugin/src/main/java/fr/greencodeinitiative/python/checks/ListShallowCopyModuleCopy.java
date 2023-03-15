package fr.greencodeinitiative.python.checks;

import fr.greencodeinitiative.python.checks.helpers.TreeHelper;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.symbols.Usage;
import org.sonar.plugins.python.api.tree.*;

import java.util.*;

@Rule(
        key = "EBOT001",
        name = "Developpement",
        description = ListShallowCopyModuleCopy.MESSAGE_RULE,
        priority = Priority.MAJOR,
        tags = {"bug"})
public class ListShallowCopyModuleCopy extends PythonSubscriptionCheck {

    private static final List<String> COPY_LIB = Arrays.asList("copy");

    protected static final String MESSAGE_RULE = "Using `copy.copy(x)` of `module copy` to perform a shallow copy of a list is not energy efficient.";

    protected static final String FUNCTION_TO_CHECK = "copy";
    protected static final String MODULE_TO_CHECK = "copy";

    private boolean isUsingModule = false;

    /**
     * Register FILE_INPUT NODE to check if the module import statement is present
     * Register ARG_LIST NODE to check if the first argument of the function is a list
     *
     * @param context The context of the analyser
     *
     */
    @Override
    public void initialize(Context context) {
        // Parse the FILE_INPUT Tree to check if the copy module is present in the file
        context.registerSyntaxNodeConsumer(Tree.Kind.FILE_INPUT, this::visitFile);

        //Parse the ARG_LIST Tree to access the arguments of the functions
        context.registerSyntaxNodeConsumer(Tree.Kind.ARG_LIST, this::checkArgumentsList);
    }

    /**
     * Check the modules imported in the files
     *
     * @param ctx The context that subscribes to the FILE_INPUT node
     */
    private void visitFile(SubscriptionContext ctx) {
        FileInput tree = (FileInput) ctx.syntaxNode();
        SymbolsFromImport visitor = new SymbolsFromImport();
        tree.accept(visitor);
        visitor.symbols.stream()
                .filter(Objects::nonNull)
                .map(Symbol::fullyQualifiedName)
                .filter(Objects::nonNull)
                .forEach(qualifiedName -> {
                    // If file contains copy module set isUsingModule to True
                    if (COPY_LIB.contains(qualifiedName)) {
                        isUsingModule = true;
                    }
                });
    }

    /**
     * Tree Visitor for imported modules
     */
    private static class SymbolsFromImport extends BaseTreeVisitor {
        private final Set<Symbol> symbols = new HashSet<>();

        @Override
        public void visitAliasedName(AliasedName aliasedName) {
            List<Name> names = aliasedName.dottedName().names();
            symbols.add(names.get(names.size() - 1).symbol());
        }
    }

    /**
     * Check the arguments tree
     * If the copy module is imported
     * And the parent is the copy function of the copy module
     * And the first argument of the methode is a list
     * report an issue
     *
     * @param context The context that subscribes to the ARG_LIST node
     */
    private void checkArgumentsList(SubscriptionContext context) {
        //We get here if the Tree parsed is an ArgList Tree
        ArgList argumentList = (ArgList) context.syntaxNode();

        //Check that the copy module is present in the file and the parent of the argument_list tree is a function
        if (isUsingModule && argumentList.parent().is(Tree.Kind.CALL_EXPR)) {
            //Check that the method called is copy.copy()
            if (TreeHelper.isMethodCalled(((CallExpression) argumentList.parent()), MODULE_TO_CHECK, FUNCTION_TO_CHECK)) {
                if (((CallExpression) argumentList.parent()).arguments().get(0).equals(argumentList.arguments().get(0))) {
                    Argument argument = argumentList.arguments().get(0);
                    if (argument.is(Tree.Kind.REGULAR_ARGUMENT)) {
                        RegularArgument regularArgument = (RegularArgument) argument;

                        //Check the argument, is it a Variable ?
                        if (regularArgument.expression().is(Tree.Kind.NAME)) {
                            Name name = ((Name) regularArgument.expression());
                            if (name.isVariable()) {
                                for (Usage usage : name.symbol().usages()) {
                                    if (usage.kind().equals(Usage.Kind.ASSIGNMENT_LHS)) {
                                        //Check Parent recursively to know if the Variable is a list
                                        checkParent(context, argumentList.parent(), usage.tree());

                                    }
                                }
                            }

                        //Check the argument, is it an Expression ?
                        } else if (regularArgument.expression().is(Tree.Kind.CALL_EXPR)) {
                            //Check Parent recursively to check if the expression return a list
                            checkParent(context, argumentList.parent(), regularArgument.expression());

                        } else {
                            System.out.println("not the write type :: " + regularArgument.expression().getKind().name());
                        }

                    } else {
                        System.out.println("nothing  " + argument.getKind());
                    }
                } else {
                    System.out.println("this is not my argument");
                }
            }
        }
    }

    /**
     * Recursive Method checking the type of the argument of the function
     *
     * @param ctx The context that subscribes to the ARG_LIST node
     * @param expression the expression that needs to be reported
     * @param tree the tree containing the argument member
     */
    private void checkParent(SubscriptionContext ctx, Tree expression, Tree tree) {
        if (tree.parent().is(Tree.Kind.ASSIGNMENT_STMT)) {
            AssignmentStatement assignmentStatement = (AssignmentStatement) tree.parent();
            if (assignmentStatement.assignedValue().type().mustBeOrExtend("list")) {
                ctx.addIssue(expression, ListShallowCopyModuleCopy.MESSAGE_RULE);
            }
        } else {
            checkParent(ctx, expression, tree.parent());
        }
    }
}