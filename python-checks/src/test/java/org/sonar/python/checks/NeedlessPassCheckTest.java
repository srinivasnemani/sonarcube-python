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

import org.junit.jupiter.api.Test;
import org.sonar.python.checks.quickfix.PythonQuickFixVerifier;
import org.sonar.python.checks.utils.PythonCheckVerifier;

class NeedlessPassCheckTest {

  @Test
  void test() {
    PythonCheckVerifier.verify("src/test/resources/checks/needlessPass.py", new NeedlessPassCheck());
  }

  @Test
  void quick_fix_test() {
    var expected = "def my_method():\n" +
      "    print('foo')\n" +
      "    print('foo')\n";

    var input = "def my_method():\n" +
      "    print('foo')\n" +
      "    print('foo')\n" +
      "    pass\n";
    PythonQuickFixVerifier.verify(new NeedlessPassCheck(), input, expected);
    PythonQuickFixVerifier.verifyQuickFixMessages(new NeedlessPassCheck(), input, NeedlessPassCheck.QUICK_FIX_MESSAGE);

    input = "def my_method():\n" +
      "    print('foo')\n" +
      "    pass\n" +
      "    print('foo')\n";
    PythonQuickFixVerifier.verify(new NeedlessPassCheck(), input, expected);


    expected = "def my_method():\n" +
      "    print('foo'); print('foo')\n";

    input = "def my_method():\n" +
      "    print('foo'); pass; print('foo')\n";
    PythonQuickFixVerifier.verify(new NeedlessPassCheck(), input, expected);

    input = "def my_method():\n" +
      "    print('foo'); print('foo'); pass\n";
    PythonQuickFixVerifier.verify(new NeedlessPassCheck(), input, expected);
  }

}
