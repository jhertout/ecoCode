package fr.greencodeinitiative.python.checks;

import org.junit.Test;
import org.sonar.python.checks.utils.PythonCheckVerifier;

public class PreferUsingSlots {

    @Test
    public void test() {
        PythonCheckVerifier.verify("src/test/resources/checks/avoidNotUsingSlots.py", new SlotsCheck());
    }
}
