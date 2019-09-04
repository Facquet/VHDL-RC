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

package com.linty.sonar.plugins.vhdlrc;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.utils.Version;

import com.google.common.collect.ImmutableList;
import com.linty.sonar.plugins.vhdlrc.metrics.MetricSensor;
import com.linty.sonar.plugins.vhdlrc.rules.VhdlRulesDefinition;
import com.linty.sonar.zamia.BuildPathMaker;

public class VHDLRcPlugin implements Plugin {

 public static final Version SQ_6_7 = Version.create(7, 4);
 private static final String VHDL_RULECHEKER_SUBCATEGORY = "VHDL RuleChecker";
	
 @Override 
 public void define(Context context) {
   
  ImmutableList.Builder<Object> builder = ImmutableList.builder();
      if (!context.getSonarQubeVersion().isGreaterThanOrEqual(SQ_6_7)) {
        throw new IllegalStateException("SonarQube " + SQ_6_7.major() + "." + SQ_6_7.minor() + " is required for VHDLRC plugin");
      }
      builder.add(
      Vhdl.class,
      VhdlRulesDefinition.class,
      VhdlRcProfile.class,
      VhdlRcSensor.class,
      MetricSensor.class
        );
	    builder.add(PropertyDefinition.builder(Vhdl.FILE_SUFFIXES_KEY)
	      .category(Vhdl.VHDLRC_CATEGORY)
	      .subCategory("General")
	      .defaultValue(Vhdl.DEFAULT_FILE_SUFFIXES)
	      .name("File suffixes")
	      .index(1)
	      .multiValues(true)
	      .description("Comma-separated list of suffixes for files to analyze. To not filter, leave the list empty.")
	      .onQualifiers(Qualifiers.PROJECT)
	      .build());
	    builder.add(PropertyDefinition.builder(VhdlRcSensor.SCANNER_HOME_KEY)
	      .category(Vhdl.VHDLRC_CATEGORY)
	      .subCategory(VHDL_RULECHEKER_SUBCATEGORY)
	      .name("RuleChecker Path")
	      .hidden()
	      .build());
	    builder.add(PropertyDefinition.builder(BuildPathMaker.TOP_ENTITY_KEY)
	      .category(Vhdl.VHDLRC_CATEGORY)
        .subCategory("BuildPath")
        .name("Top Entities")
        .description("Toplevel Entity \r\n" + "Format:  LIBRARY.ENTITY(ARCHITECTURE) \r\n" + "Example: WORK.My_entity(Rtl)")
        .index(2)
        .defaultValue(BuildPathMaker.DEFAULT_ENTITY)
        .onQualifiers(Qualifiers.PROJECT)
        .build());
	    builder.add(PropertyDefinition.builder(BuildPathMaker.CUSTOM_CMD_KEY)
	      .category(Vhdl.VHDLRC_CATEGORY)
        .subCategory("BuildPath")
        .name("Custom Commands")
        .description(BuildPathMaker.customCmdDescription())
        .index(3)
        .multiValues(false)
        .onQualifiers(Qualifiers.PROJECT)
        .type(PropertyType.TEXT)
        .build());
	  context.addExtensions(builder.build());
  }
}
