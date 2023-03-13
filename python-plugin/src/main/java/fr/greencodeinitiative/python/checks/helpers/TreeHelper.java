package fr.greencodeinitiative.python.checks.helpers;

import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.RegularArgument;
import org.sonar.plugins.python.api.tree.Tree;

public class TreeHelper {

    private TreeHelper() {
        // Class cannot be instantiated
    }

    public static String getFullyQualifiedName(CallExpression expression) {
        String fullyQualifiedName = "";
        if (expression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
            Symbol symbol = ((QualifiedExpression) expression.callee()).symbol();
            if (symbol != null) {
                fullyQualifiedName = symbol.fullyQualifiedName();
            }
        }
        return fullyQualifiedName;
    }

    public static String getMethodName(CallExpression expression) {
        String methodName = "";
        if (expression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
            methodName = ((QualifiedExpression) expression.callee()).name().name();
        }
        return methodName;
    }

    public static String getClassName(CallExpression expression) {
        String methodName = "";
        String fullyQualifiedName = getFullyQualifiedName(expression);
        if (fullyQualifiedName.contains(".")) {
            methodName = fullyQualifiedName.split("\\.")[0];
        }
        return methodName;
    }

    public static boolean isMethodCalled(CallExpression expression, String methodClass, String methodName) {
        boolean result = false;
        if (expression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
            if (TreeHelper.getClassName(expression).equals(methodClass) && TreeHelper.getMethodName(expression).equals(methodName)) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isMethodCalled(CallExpression expression, String methodClass, String methodName, String firstParameterType) {
        boolean result = false;
        if (isMethodCalled(expression, methodClass, methodName)) {
            if (!expression.arguments().isEmpty()) {
                if (expression.arguments().get(0).is(Tree.Kind.REGULAR_ARGUMENT)) {
                    RegularArgument arg = ((RegularArgument) expression.arguments().get(0));
                    if (arg.expression().type().canBeOrExtend(firstParameterType)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

}
