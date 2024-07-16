

	
Building:
	uses maven (pom.xml)	
	
Eclipse:
	convert to maven project
		right click on project name
			choose menu itme: "Configure"
				select: "Convert to Maven Project"
			

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
	
WebServer / REST / Swagger
    need to set the webapp directory, by setting JettyDirectory in the server.ini file.
        ./runtime/server/server.ini
            JettyDirectory=C:\\Users\\vvia\\git\\oatemplate\\src\\main\\webapp  
    REST API
        http://127.0.0.1:8080/servlet/oarest/{className}/{Id}   
    Swagger
        http://127.0.0.1:8080/swagger-ui/index.html 
    see JettyController.java for other services, servlets - REST, webservices, images, etc.
	
	Note: port is based on property value:  JettyPort, default is 8080.  The console displays ports at server startup. 

Images for JFC application





================= Add application to OAAppStore =================

This is to use any OA Application from OAAppStore

see: OAAppStore-Installer, for the App Store Windows (uses jpackage) installer.  

    create jar files
    maven clean install

    copy [project]-0.0.1.jar to OAAppStore-Run/jarstore/[project url]

    if there are new oa-* jar files, then copy them to OAAppStore-Run/jarstore/com/viaoa
     
    update OAAppStore-Run/appstore/[project url]/version.ini
        update version, release and the files to download
        release needs to match the value from file values.properties "release" property











