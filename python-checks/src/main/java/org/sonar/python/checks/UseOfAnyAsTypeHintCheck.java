/*
 * SonarQube Python Plugin
 * Copyright (C) 2011-2023 SonarSource SA
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
package org.sonar.python.checks;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.plugins.python.api.PythonSubscriptionCheck;
import org.sonar.plugins.python.api.SubscriptionContext;
import org.sonar.plugins.python.api.tree.Decorator;
import org.sonar.plugins.python.api.tree.FunctionDef;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.TypeAnnotation;
import org.sonar.python.semantic.SymbolUtils;
import org.sonar.python.tree.TreeUtils;

@Rule(key = "S6542")
public class UseOfAnyAsTypeHintCheck extends PythonSubscriptionCheck {

  private static final String MESSAGE = "Use a more specific type than `Any` for this type hint.";
  private static final Set<String> OVERRIDE_FQNS = Set.of("typing.override", "typing.overload");

  @Override
  public void initialize(Context context) {
    context.registerSyntaxNodeConsumer(Tree.Kind.RETURN_TYPE_ANNOTATION, UseOfAnyAsTypeHintCheck::checkForAnyInReturnTypeAndParameters);
    context.registerSyntaxNodeConsumer(Tree.Kind.PARAMETER_TYPE_ANNOTATION, UseOfAnyAsTypeHintCheck::checkForAnyInReturnTypeAndParameters);
    context.registerSyntaxNodeConsumer(Tree.Kind.VARIABLE_TYPE_ANNOTATION, UseOfAnyAsTypeHintCheck::checkForAnyInTypeHint);
  }

  private static void checkForAnyInTypeHint(SubscriptionContext ctx) {
    Optional.of((TypeAnnotation) ctx.syntaxNode())
      .filter(UseOfAnyAsTypeHintCheck::isTypeAny)
      .ifPresent(typeAnnotation -> ctx.addIssue(typeAnnotation.expression(), MESSAGE));
  }

  private static void checkForAnyInReturnTypeAndParameters(SubscriptionContext ctx) {
    TypeAnnotation typeAnnotation = (TypeAnnotation) ctx.syntaxNode();
    Optional.of(typeAnnotation)
      .filter(UseOfAnyAsTypeHintCheck::isTypeAny)
      .map(annotation -> (FunctionDef) TreeUtils.firstAncestorOfKind(annotation, Tree.Kind.FUNCDEF))
      .filter(Predicate.not(UseOfAnyAsTypeHintCheck::hasFunctionOverrideOrOverloadDecorator))
      .filter(Predicate.not(UseOfAnyAsTypeHintCheck::canFunctionBeAnOverride))
      .ifPresent(functionDef -> ctx.addIssue(typeAnnotation.expression(), MESSAGE));
  }

  private static boolean isTypeAny(@Nullable TypeAnnotation typeAnnotation) {
    return Optional.ofNullable(typeAnnotation)
      .map(annotation -> "typing.Any".equals(TreeUtils.fullyQualifiedNameFromExpression(annotation.expression())))
      .orElse(false);
  }

  private static boolean hasFunctionOverrideOrOverloadDecorator(FunctionDef currentFunctionDef) {
    return currentFunctionDef.decorators().stream()
      .map(Decorator::expression)
      .map(TreeUtils::fullyQualifiedNameFromExpression)
      .filter(Objects::nonNull)
      .anyMatch(OVERRIDE_FQNS::contains);
  }

  private static boolean canFunctionBeAnOverride(FunctionDef currentMethodDef) {
    return Optional.ofNullable(TreeUtils.getFunctionSymbolFromDef(currentMethodDef))
      .map(SymbolUtils::canBeAnOverridingMethod)
      .orElse(false);
  }

}
