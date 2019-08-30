/*
 * SonarQube Python Plugin
 * Copyright (C) 2011-2019 SonarSource SA
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

import com.sonar.sslr.api.AstNode;
import java.util.Collections;
import java.util.List;
import org.sonar.python.api.tree.PyDottedNameTree;
import org.sonar.python.api.tree.PyNameTree;
import org.sonar.python.api.tree.PyTreeVisitor;
import org.sonar.python.api.tree.Tree;

public class PyDottedNameTreeImpl extends PyTree implements PyDottedNameTree {
  private final List<PyNameTree> names;

  public PyDottedNameTreeImpl(AstNode astNode, List<PyNameTree> names) {
    super(astNode);
    this.names = names;
  }

  @Override
  public List<PyNameTree> names() {
    return names;
  }

  @Override
  public Kind getKind() {
    return Kind.DOTTED_NAME;
  }

  @Override
  public void accept(PyTreeVisitor visitor) {
    visitor.visitDottedName(this);
  }

  @Override
  public List<Tree> children() {
    return Collections.unmodifiableList(names);
  }
}