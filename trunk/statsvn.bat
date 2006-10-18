svn log -v --xml > logfile.log
mkdir target\docs\statsvn
java -jar c:\java\statsvn\statsvn.jar -output-dir target\docs\statsvn -title JTreeMap ./logfile.log . 