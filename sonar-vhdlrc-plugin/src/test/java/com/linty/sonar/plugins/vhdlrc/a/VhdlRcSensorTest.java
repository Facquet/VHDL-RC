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

package com.linty.sonar.plugins.vhdlrc.a;



import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import com.linty.sonar.plugins.vhdlrc.VHDLRcPlugin;
import com.linty.sonar.plugins.vhdlrc.VhdlRcSensor;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.internal.apachecommons.io.FilenameUtils;
import org.sonar.api.rule.RuleKey;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class VhdlRcSensorTest  {
	
	private static final SonarRuntime SQ67 = SonarRuntimeImpl.forSonarQube(VHDLRcPlugin.SQ_6_7, SonarQubeSide.SERVER, SonarEdition.COMMUNITY);
	private static final String PROJECT_ID = "vhdlrc-test";
	private VhdlRcSensor sensor = new VhdlRcSensor();
	
  private SensorContextTester context1 = createContext("src/test/files","src/test/files/scanner-home");
	
	@Before
	public void init() {
		addRules(context1,"STD_00400","STD05000");
		addTestFile(context1,"I2C/file1.vhd","This file has one line");
		addTestFile(context1,"I2C/file3.vhd","This one has \r\n 3 \r\nlines");
		addTestFile(context1,"I2C/file_no_issues.vhd","");
		addTestFile(context1,"Top.vhd","One line");
	}
	
	@Rule
	public LogTester logTester = new LogTester();
	
//Undefined path for scanner means no rule checker analysis should be performed
	@Test
  public void test_descriptor() {
    DefaultSensorDescriptor sensorDescriptor = new DefaultSensorDescriptor();
    sensor.describe(sensorDescriptor);
    assertThat(sensorDescriptor.name()).isEqualTo("vhdlRcSensor");
    MapSettings settings = new MapSettings();
    assertThat(sensorDescriptor.configurationPredicate().test(settings.asConfig())).isFalse();
    assertThat(settings.asConfig().hasKey((VhdlRcSensor.SCANNER_HOME_KEY))).isFalse();
    settings.setProperty(VhdlRcSensor.SCANNER_HOME_KEY, "C:Tools/sonar-scanner/rc");
    assertThat(sensorDescriptor.configurationPredicate().test(settings.asConfig())).isTrue();
  }
	
	@Test (expected = IllegalStateException.class)
	public void Unset_parameter_should_never_occure() {
	  SensorContextTester Context = SensorContextTester.create(Paths.get("src/test/files"));
	  sensor.execute(Context);
	}
	

	@Test
  public void invalid_path_should_log_error() {
    SensorContextTester Context = createContext("src/test/files","src/invalid/path/");
    sensor.execute(Context);
    List<Issue> issues = new ArrayList<>(Context.allIssues());
    assertThat(issues).isEmpty();
    assertThat(logTester.logs()).isNotEmpty();
  }
  
	@Test
	public void test_two_good_issues_one_failure() {
	  sensor.execute(context1);
	  List<Issue> issues = new ArrayList<>(context1.allIssues());
	  
	  assertThat(issues).hasSize(4);
	  assertNoIssueOnFile(context1,"file_no_issues.vhd");	 
	  
	  
	  Issue issue1 = issues.get(2);
	  assertThat(issue1.ruleKey().rule()).isEqualTo("STD_00400");
	  assertThat(issue1.primaryLocation().inputComponent().key()).isEqualTo(PROJECT_ID + ":I2C/file1.vhd");
	  assertThat(issue1.primaryLocation().textRange().start().line()).isEqualTo(1);
	  assertThat(issue1.primaryLocation().message()).isEqualTo("Label is missing");
	  
	  Issue issue2 = issues.get(3);
	  assertThat(issue2.ruleKey().rule()).isEqualTo("STD_00400");
    assertThat(issue2.primaryLocation().inputComponent().key()).isEqualTo(PROJECT_ID + ":I2C/file3.vhd");
    assertThat(issue2.primaryLocation().textRange().start().line()).isEqualTo(2);
    assertThat(issue2.primaryLocation().message()).isEqualTo("Label is missing 2");
    
    String no_file = FilenameUtils.separatorsToSystem("./I2C/Mux/no_file.vhd");
     
    Issue issue3 = issues.get(1);
    assertThat(issue3.ruleKey().rule()).isEqualTo("STD_05000");
    assertThat(issue3.primaryLocation().inputComponent().key()).isEqualTo(PROJECT_ID + ":I2C/file1.vhd");
    assertThat(issue3.primaryLocation().textRange().start().line()).isEqualTo(1);
    assertThat(issue3.primaryLocation().message()).isEqualTo("Signal I_VZ_CMD should not be in the sensitivity list of the process");
    
    //assertThat(logTester.logs(LoggerLevel.INFO).get(1)).contains("Importing rc_report_rule_STD_03800.xml");   
//    assertThat(logTester.logs(LoggerLevel.WARN).get(2)).contains("Can't import an issue from rc_report_rule_STD_05000.xml : 65 is not a valid line for pointer. File I2C/file3.vhd has 3 line(s)");
//    assertThat(logTester.logs(LoggerLevel.WARN).get(3)).contains("Input file not found : "+ no_file + ". No rc issues will be imported on this file.");  
//    assertThat(logTester.logs(LoggerLevel.ERROR).get(0)).isNotEmpty();
    //assertThat(logTester.logs(LoggerLevel.WARN).size()).isEqualTo(2);
	}
	
	
	public static void assertNoIssueOnFile(SensorContextTester context, String fileName) {
	  for(Issue i: context.allIssues()) {
	    assertThat(i.primaryLocation().inputComponent().key()).isNotEqualTo(PROJECT_ID + ":" + fileName);
	  }
	}
	
	public static SensorContextTester createContext(String projectHomePath, String scannerHomePath) {
    return SensorContextTester.create(Paths.get(projectHomePath))
      .setSettings(new MapSettings()
        .setProperty(VhdlRcSensor.SCANNER_HOME_KEY, scannerHomePath)
        )
      .setRuntime(SQ67);
	}
	
	public static void addRules(SensorContextTester context, String...args) {
	  ActiveRulesBuilder builder = new ActiveRulesBuilder();
	  for(String ruleKey : args) {
	    builder.addRule(
	    		new NewActiveRule.Builder()
	    		.setRuleKey(RuleKey.of("vhdlrc-repository",ruleKey))
	    		.setLanguage("vhdl")
	    		.build()
	    		);
	  }
    context.setActiveRules(builder.build());	  
	}
	
	public static void addTestFile(SensorContextTester context, String filePath, String content) {
	  context.fileSystem().add(TestInputFileBuilder.create(PROJECT_ID,filePath)
      .setLanguage("vhdl")
      .setCharset(UTF_8)
      .setContents(content)
      .build());
	}
}
	
