for Windows

 JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF8'  sbt "run -Dconfig.file=c:\Users\username\apps\config\app.conf"


for Linux
sbt run -Dconfig.file=/full/path/app.conf

Create an app.conf file containing this

include "application.conf"
application.secret="someGeneratedSecret" 
stationAPI.apiKey = "someKey"

javac (java jdk) must be on the path 
