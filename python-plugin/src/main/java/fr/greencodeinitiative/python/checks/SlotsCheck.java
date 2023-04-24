package fr.greencodeinitiative.python.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.symbols.Symbol;
import org.sonar.plugins.python.api.tree.*;

@Rule(
        key = SlotsCheck.RULE_KEY,
        name = SlotsCheck.RULE_NAME,
        description = SlotsCheck.MESSAGE_RULE,
        priority = Priority.MINOR,
        tags = {"bug",
                "eco-design",
                "optimized-api",
                "environment",
                "ecocode"})

public class SlotsCheck extends PythonSubscriptionCheck {

    protected static final String MESSAGE_RULE = "You should using Slots to explicitly declare data members and use way less memory than the default behavior based on __dict__ and __weakref__ attributes.";

    public static final String RULE_KEY = "EC88";

    public static final String RULE_NAME = "Slots";


    @Override
    public void initialize(Context context) {
        context.registerSyntaxNodeConsumer(Tree.Kind.FILE_INPUT, this::visitFileInput);
    }

    private void visitFileInput(SubscriptionContext subscriptionContext) {
        FileInput input = (FileInput) subscriptionContext.syntaxNode();
        if (input.statements() != null && input.statements().statements().size() > 0) {
            for (Statement statement : input.statements().statements()) {
                //check class def
                boolean isDataClassSlotPresent = false;
                boolean isFieldSlotPresent = false;
                if (statement.is(Tree.Kind.CLASSDEF)) {
                    ClassDef classDef = (ClassDef) statement;
                    //check decorator dataclass
                    if (classDef.decorators().size() > 0) {
                        for (Decorator decorator : classDef.decorators()) {
                            if (decorator.arguments() != null && decorator.arguments().arguments().size() > 0) {
                                for (Argument argument : decorator.arguments().arguments()) {
                                    if (argument.is(Tree.Kind.REGULAR_ARGUMENT)) {
                                        RegularArgument arg = (RegularArgument) argument;
                                        if (arg.keywordArgument().name().equals("slots")) {
                                            if (arg.expression().is(Tree.Kind.NAME)) {
                                                Name name = (Name) arg.expression();
                                                if (name.name().equals("True")) {
                                                    //dataclass slot is present
                                                    isDataClassSlotPresent = true;
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                    if (classDef.classFields() != null && !classDef.classFields().isEmpty()) {
                        for (Symbol classField : classDef.classFields()) {
                            if (classField.name().equals("__slots__")) {
                                //slots is present
                                isFieldSlotPresent = true;
                            }
                        }
                    }

                    if (!isDataClassSlotPresent && !isFieldSlotPresent) {
                        subscriptionContext.addIssue(classDef.name(), MESSAGE_RULE);
                    }
                }
            }
        }
    }
}

