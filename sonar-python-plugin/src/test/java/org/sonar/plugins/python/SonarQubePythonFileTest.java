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
package org.sonar.plugins.python;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.plugins.python.api.PythonFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SonarQubePythonFileTest {

  private InputFile inputFile = mock(InputFile.class, "file1.py");

  @Test
  void known_file() throws Exception {
    when(inputFile.contents()).thenReturn("Hello 6.2!");
    PythonFile pythonFile = SonarQubePythonFile.create(inputFile);
    assertThat(pythonFile.content()).isEqualTo("Hello 6.2!");
    assertThat(pythonFile.toString()).isEqualTo(inputFile.toString());
    assertThat(pythonFile.uri()).isEqualTo(inputFile.uri());
    assertThat(pythonFile.key()).isEqualTo(inputFile.key());
  }

  @Test
  void unknown_file() throws Exception {
    when(inputFile.contents()).thenThrow(new FileNotFoundException());
    PythonFile pythonFile = SonarQubePythonFile.create(inputFile);
    assertThatThrownBy(pythonFile::content).isInstanceOf(IllegalStateException.class);
  }

}
