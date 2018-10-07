for Windows

 JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF8'  sbt "run -Dconfig.file=c:\Users\alen\apps\config\sykkel.conf"


for Linux
sbt run -Dconfig.file=/full/path/app.conf


Create an app.conf file containing this

include "application.conf"
application.secret="someGeneratedSecret"
stationAPI.apiKey = "someKey"