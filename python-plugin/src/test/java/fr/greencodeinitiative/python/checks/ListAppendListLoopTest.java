package fr.greencodeinitiative.python.checks;

import org.junit.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

public class ListAppendListLoopTest {

    @Test
    public void test() {
        PythonCheckVerifier.verify("src/test/resources/checks/listAppendInLoop.py", new ListAppendInLoop());
    }

}
