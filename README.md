# VHDL-RC  

[SonarQube](https://www.sonarqube.org) plugin for VHDL using [CNES](https://cnes.fr/en) static analyzer [Zamiacad-Rulechecker](https://github.com/VHDLTool/Zamiacad-Rulechecker/wiki).  
  
VHDL-RC is a ready to use SonarQube plugin  
Call `rc-scanner` instead of `sonar-scanner`.  
The external tool is wrapped in rc-scanner and is fully taken care of.  

## Features  
* Automated analysis (no tool configuration required)  
* Issues import from [Zamiacad-Rulechecker](https://github.com/VHDLTool/Zamiacad-Rulechecker/wiki) analysis  
* 130 guidline rules with examples and diagrams, 18 implemented rules  
* Effective rule parameters  
* Loc and comments metrics computation   
* Library and Top entity configuration  

## Quickstart
1. Setup a SonarQube 7.9 LTS instance  
2. Install the plugin (see [Installation]())  
3. Install the rc-scanner  
4. Run an analysis (see [Running the analysis]())  

## License  
Copyright 2018-2019 Maxime Facquet    
Licensed under the [GNU Lesser General Public License, Version 3.0](https://www.gnu.org/licenses/lgpl.txt)