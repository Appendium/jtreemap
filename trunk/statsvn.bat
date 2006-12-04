svn log -v --xml > logfile.log
rem mkdir qalab\target\docs\statsvn
java -jar c:\java\statsvn\statsvn.jar -format xdoc -verbose -output-dir src\site\statsvn -title JTreeMap -viewvc http://svn.sourceforge.net/viewvc/jtreemap/trunk ./logfile.log .