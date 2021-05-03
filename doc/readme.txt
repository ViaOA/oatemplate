

	
Building:
	uses maven (pom.xml)	
	
Eclipse:
	convert to maven project

Setup:
	create a runtime directory:
		(under project directory, "...\git\template\runtime")
			note: the "runtime" directory should be in your .gitignore, so that you dont check in any of the files.
			
	create runtime directory for each type of runtime
		../runtime/server
		../runtime/client
		../runtime/single
					 
	copy data\*.bin to runtime\single\data
		this is the data files.
	copy data\single.ini to runtime\single
		this is the property values
		the default values can be found in src/main/java/com/template/resource/values.properties
 		single.ini will overwrite the default value defined in the values.properties file.

Running:
	java main class: 
		com.template.control.StartupController
	arguments:  "single"
	make sure that it is being ran from the runtime directory that you set up, ex: "...\git\template\runtime\single"
	
	* argument can be: server, client, single	
	
To get the Model:
	
get OABuilder:
	https://github.com/ViaOA/oabuilder-run

Model file for this project:
	model/template.obx
		* open using OABuilder
	
	
	


