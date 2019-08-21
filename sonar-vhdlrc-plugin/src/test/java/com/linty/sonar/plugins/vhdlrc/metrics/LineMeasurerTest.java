/*
 * Vhdl RuleChecker (Vhdl-rc) plugin for Sonarqube & Zamiacad
 * Copyright (C) 2019 Maxime Facquet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.linty.sonar.plugins.vhdlrc.metrics;

import com.linty.sonar.test.utils.SensorTestUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;

import static org.assertj.core.api.Assertions.assertThat;

public class LineMeasurerTest {
  
  @Rule
  public LogTester logTester = new LogTester();
  
  public Path baseDir = Paths.get("src/test/Metrics");
  
  @Test
  public void test() {
    checkLocMeasure(baseDir,"File1.vhd",45);
    checkCommentLineMeasure(baseDir,"File1.vhd",19); 
    
    checkLocMeasure(baseDir,"comment_blocks.vhd",27);
    checkCommentLineMeasure(baseDir,"comment_blocks.vhd",10);
    
    checkLocMeasure(baseDir,"empty.vhd",0);
    checkCommentLineMeasure(baseDir,"empty.vhd",0);
    
    checkLocMeasure(baseDir,"commented_out_file.vhd",0);
    checkCommentLineMeasure(baseDir,"commented_out_file.vhd",0);//Should consider header
    
    
  }
  
  @Test
  public void testExpetion() {
      try{
        LineMeasurer measurer = new LineMeasurer(SensorTestUtils.getInputFile(baseDir,"Not existing.vhd", "module"));
        measurer.analyseFile();      
      } catch (IllegalStateException ise) {
        assertThat(ise).hasMessageStartingWith("Failed to read: Not existing.vhd");
      }
  }
  
  public static void checkLocMeasure(Path baseDir, String relativeFilePath, int expected) {
    LineMeasurer measurer = new LineMeasurer(SensorTestUtils.getInputFile(baseDir,relativeFilePath, "module"));
    measurer.analyseFile();
    assertThat(measurer.getNumberLineOfCode()).isNotNull();
    assertThat(measurer.getNumberLineOfCode()).isEqualTo(expected);
  }
  
  public static void checkCommentLineMeasure(Path baseDir, String relativeFilePath, int expected) {
    LineMeasurer measurer = new LineMeasurer(SensorTestUtils.getInputFile(baseDir,relativeFilePath, "module"));
    measurer.analyseFile();
    assertThat(measurer.getNumberLineComment()).isNotNull();
    assertThat(measurer.getNumberLineComment()).isEqualTo(expected);    
  }
  
}
