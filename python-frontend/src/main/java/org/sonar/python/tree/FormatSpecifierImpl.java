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
package org.sonar.python.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sonar.plugins.python.api.tree.FormatSpecifier;
import org.sonar.plugins.python.api.tree.FormattedExpression;
import org.sonar.plugins.python.api.tree.Token;
import org.sonar.plugins.python.api.tree.Tree;
import org.sonar.plugins.python.api.tree.TreeVisitor;

public class FormatSpecifierImpl extends PyTree implements FormatSpecifier {

  private Token columnToken;
  private List<Tree> fStringMiddles;

  public FormatSpecifierImpl(Token columnToken, List<Tree> fStringMiddles) {
    this.columnToken = columnToken;
    this.fStringMiddles = fStringMiddles;
  }

  @Override
  public Token columnToken() {
    return columnToken;
  }

  @Override
  public List<FormattedExpression> formatExpressions() {
    return fStringMiddles.stream()
      .filter(FormattedExpression.class::isInstance)
      .map(FormattedExpression.class::cast)
      .collect(Collectors.toList());
  }

  @Override
  List<Tree> computeChildren() {
    List<Tree> children = new ArrayList<>();
    children.add(columnToken);
    children.addAll(fStringMiddles);
    return children;
  }

  @Override
  public void accept(TreeVisitor visitor) {
    visitor.visitFormatSpecifier(this);
  }

  @Override
  public Kind getKind() {
    return Kind.FORMAT_SPECIFIER;
  }
}
