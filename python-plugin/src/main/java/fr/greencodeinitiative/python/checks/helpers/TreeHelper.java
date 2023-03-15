package fr.greencodeinitiative.python.checks.helpers;

import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.CallExpression;
import org.sonar.plugins.python.api.tree.QualifiedExpression;
import org.sonar.plugins.python.api.tree.Tree;

public class TreeHelper {

    private TreeHelper() {
        // Class cannot be instantiated
    }

    /**
     *
     * @param expression
     * @return the fullyQualifiedName of the expression
     */
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


    /**
     *
     * @param expression the function to check
     * @return the name of the function in param
     */
    public static String getMethodName(CallExpression expression) {
        String methodName = "";
        if (expression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
            methodName = ((QualifiedExpression) expression.callee()).name().name();
        }
        return methodName;
    }

    /**
     *
     * @param expression the function to check
     * @return the name of the owner class
     */
    public static String getClassName(CallExpression expression) {
        String methodName = "";
        String fullyQualifiedName = getFullyQualifiedName(expression);
        if (fullyQualifiedName != null && fullyQualifiedName.contains(".")) {
            methodName = fullyQualifiedName.split("\\.")[0];
        }
        return methodName;
    }

    /**
     *
     * @param expression the function to check
     * @param methodClass the owner class of the expected method
     * @param methodName the name of th expected method
     * @return if the expression is the method expected
     */
    public static boolean isMethodCalled(CallExpression expression, String methodClass, String methodName) {
        boolean result = false;
        if (expression.callee().is(Tree.Kind.QUALIFIED_EXPR)) {
            if (TreeHelper.getClassName(expression).equals(methodClass) && TreeHelper.getMethodName(expression).equals(methodName)) {
                result = true;
            }
        }
        return result;
    }

}
