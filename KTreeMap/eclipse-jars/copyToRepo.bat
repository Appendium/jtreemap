call mkdir "%HOMEPATH%\.maven\repository\org.eclipse"
call mkdir "%HOMEPATH%\.maven\repository\org.eclipse\jars"
xcopy /Y *.jar "%HOMEPATH%\.maven\repository\org.eclipse\jars\"

