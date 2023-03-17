package fr.greencodeinitiative.python.checks;

import org.junit.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

public class ListComprehensionTest {

    @Test
    public void test() {
        PythonCheckVerifier.verify("src/test/resources/checks/listComprehension.py", new ListComprehension());
    }

}
