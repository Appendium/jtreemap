svn log -v --xml https://jtreemap.svn.sourceforge.net/svnroot/jtreemap/ > logfile.log
java -jar c:\java\statsvn\statsvn.jar -exclude "**/qalab.xml" -tags "^1.1.0|^1.0.0" -xdoc -config-file statsvn.properties -verbose -output-dir src\site\statsvn -title JTreeMap -viewvc http://svn.sourceforge.net/viewvc/jtreemap/trunk ./logfile.log .