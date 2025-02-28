/*
 * ecoCode - Java language - Provides rules to reduce the environmental footprint of your Java programs
 * Copyright © 2023 Green Code Initiative (https://www.ecocode.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

public class AvoidConcatenateStringsInLoopTest {

    @Test
    void checkNonCompliantTests() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/AvoidConcatenateStringsInLoop.java")
                .withCheck(new AvoidConcatenateStringsInLoop())
                .verifyIssues();
    }

    @Test
    void checkCompliantTests() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/GoodWayConcatenateStringsLoop.java")
                .withCheck(new AvoidConcatenateStringsInLoop())
                .verifyNoIssues();
    }

}
