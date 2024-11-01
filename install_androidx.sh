#!/bin/sh
export androidx_documentfile_version=1.0.1
rm -f ./target/documentfile-${androidx_documentfile_version}.aar
rm -rf ./target/androidx_documentfile
wget -q -P ./target https://dl.google.com/android/maven2/androidx/documentfile/documentfile/${androidx_documentfile_version}/documentfile-${androidx_documentfile_version}.aar
unzip -d ./target/androidx_documentfile ./target/documentfile-${androidx_documentfile_version}.aar classes.jar
mvn install:install-file -Dfile=./target/androidx_documentfile/classes.jar -DgroupId=net.ibbaa.phonelog.android -DartifactId=documentfile_repackage -Dversion=${androidx_documentfile_version} -Dpackaging=jar -DgeneratePom=true
rm -f ./target/documentfile-${androidx_documentfile_version}.aar
rm -rf ./target/androidx_documentfile