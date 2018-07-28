package com.linty.sonar.plugins.vhdlrc.issues;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.LoggerLevel;

public class ExternalReportProviderTest {

	@Rule
	public LogTester logTester = new LogTester();
	
	@Test
	public void test() {
		List<Path> paths = ExternalReportProvider.getReportFiles(Paths.get("src/test/files/log/reporting/rule"));
		assertThat(paths).hasSize(15);
	}
	
	@Test
	public void test_error() {
		List<Path> paths = ExternalReportProvider.getReportFiles(Paths.get("src/test/not_existing"));
		assertThat(logTester.logs(LoggerLevel.ERROR)).isNotEmpty();
		//System.out.println(logTester.logs(LoggerLevel.ERROR).get(0));
	}
	
	@Test
	public void test_error_with_debug_enable_should_log_stack_trace() {
		logTester.setLevel(LoggerLevel.DEBUG);
		List<Path> paths = ExternalReportProvider.getReportFiles(Paths.get("src/test/not_existing"));
		assertThat(logTester.logs(LoggerLevel.DEBUG)).isNotEmpty();
		assertThat(paths).isEmpty();
		//System.out.println(logTester.logs(LoggerLevel.ERROR).get(0));
	}
	
	

}