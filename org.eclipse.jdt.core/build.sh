#!/bin/bash

sources() {
	git clone git://git.eclipse.org/gitroot/jdt/eclipse.jdt.core.git
	cd eclipse.jdt.core
	git checkout R4_3_maintenance_Java8
	cd ..
	cp -R eclipse.jdt.core/org.eclipse.jdt.core/*/org ./
	jar cf org.eclipse.jdt.core-3.9.50.v20140317-1741-sources.jar org
	rm -Rf eclipse.jdt.core org
}

binary() {
	wget http://mirror.switch.ch/eclipse/eclipse/downloads/drops4/P20140317-1600/java8patch-P20140317-1600-repository.zip
	mkdir bin
	unzip java8patch-P20140317-1600-repository.zip -d bin
	cp bin/plugins/org.eclipse.jdt.core_3.9.50.v20140317-1741.jar org.eclipse.jdt.core-3.9.50.v20140317-1741.jar
	rm -Rf java8patch-P20140317-1600-repository.zip bin
}

javadoc() {
	echo 'For javadoc, please see the source jar' > README
	jar cf org.eclipse.jdt.core-3.9.50.v20140317-1741-javadoc.jar README
	rm README
}

sign() {
	gpg --armor --detach-sign pom.xml
	mv pom.xml.asc org.eclipse.jdt.core-3.9.50.v20140317-1741.pom.asc
	gpg --armor --detach-sign org.eclipse.jdt.core-3.9.50.v20140317-1741.jar
	gpg --armor --detach-sign org.eclipse.jdt.core-3.9.50.v20140317-1741-sources.jar
	gpg --armor --detach-sign org.eclipse.jdt.core-3.9.50.v20140317-1741-javadoc.jar
}

set -e
sources
binary
javadoc
sign
