/*
 * SonarQube Python Plugin
 * Copyright (C) 2012-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package fr.greencodeinitiative.python;

import fr.greencodeinitiative.python.checks.*;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.sonar.plugins.python.api.PythonCustomRuleRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PythonRuleRepository implements RulesDefinition, PythonCustomRuleRepository {

    public static final String LANGUAGE = "py";
    public static final String NAME = "Green Code Initiative";
    public static final String RESOURCE_BASE_PATH = "/fr/greencodeinitiative/l10n/python/rules/python/";
    public static final String REPOSITORY_KEY = "gci-python";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(repositoryKey(), LANGUAGE).setName(NAME);

        new RulesDefinitionAnnotationLoader().load(repository, checkClasses().toArray(new Class[]{}));

        // technical debt
        Map<String, String> remediationCosts = new HashMap<>();
        remediationCosts.put(AvoidSQLRequestInLoop.RULE_KEY, "10min");
        remediationCosts.put(AvoidFullSQLRequest.RULE_KEY, "20min");
        repository.rules().forEach(rule -> {
            String debt = remediationCosts.get(rule.key());

            // TODO DDC : create support to use org.apache.commons.lang.StringUtils
//      if (StringUtils.isBlank(debt)) {
            if (debt == null || debt.trim().equals("")) {
                // default debt to 5min for issue correction
                rule.setDebtRemediationFunction(
                        rule.debtRemediationFunctions().constantPerIssue("5min"));
            } else {
                rule.setDebtRemediationFunction(
                        rule.debtRemediationFunctions().constantPerIssue(debt));
            }
        });

        // HTML description
        repository.rules().forEach(rule ->
                rule.setHtmlDescription(loadResource(RESOURCE_BASE_PATH + rule.key() + ".html")));

        repository.done();
    }

    @Override
    public String repositoryKey() {
        return REPOSITORY_KEY;
    }

    @Override
    public List<Class> checkClasses() {
        return Arrays.asList(
                AvoidGettersAndSetters.class,
                AvoidGlobalVariableInFunctionCheck.class,
                AvoidSQLRequestInLoop.class,
                AvoidTryCatchFinallyCheck.class,
                NoFunctionCallWhenDeclaringForLoop.class,
                AvoidFullSQLRequest.class,
                ListShallowCopyModuleCopy.class,
                ListDeepCopyWarning.class,
                ListAppendInLoop.class,
                ListComprehension.class,
                StringConcatenationFormatCheck.class,
                StringConcatenationModuloCheck.class,
                StringConcatenationJoinCheck.class
        );
    }

    private String loadResource(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (InputStream in = resource.openStream()) {
            byte[] buffer = new byte[1024];
            for (int len = in.read(buffer); len != -1; len = in.read(buffer)) {
                result.write(buffer, 0, len);
            }
            return new String(result.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource: " + path, e);
        }
    }
}