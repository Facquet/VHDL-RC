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

package com.linty.sonar.plugins.vhdlrc.utils;


import java.io.File;
import org.sonar.api.platform.ServerFileSystem;


public class ServerFileSystemTester implements ServerFileSystem{
	
	private File serverHome;
	
	
	public ServerFileSystemTester(File serverHome) {
		this.serverHome = serverHome;
	}
	
	@Override
	public File getHomeDir() {		
		return serverHome;	
	}
	
	@Override
	public File getTempDir() {		
		return serverHome;
	}
	
	
	
}
