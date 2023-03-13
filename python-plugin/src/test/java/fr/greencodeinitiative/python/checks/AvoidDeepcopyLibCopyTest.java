package fr.greencodeinitiative.python.checks;

import org.junit.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

public class AvoidDeepcopyLibCopyTest {

    @Test
    public void test() {
        PythonCheckVerifier.verify("src/test/resources/checks/avoidCopyDeepcopy.py", new AvoidCopyLibDeepcopy());
    }

}
