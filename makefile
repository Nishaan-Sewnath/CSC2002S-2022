.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
DOCDIR=doc

${BINDIR}/%.class:${SRCDIR}/%.java
	javac $< -cp ${BINDIR} -d ${BINDIR}

CLASSES=MedianFilterParallel.class MedianFilterSerial.class MeanFilterParallel.class MeanFilterSerial.class

CLASS_FILES=${CLASSES:%.class=${BINDIR}/%.class}

default: ${CLASS_FILES}

clean:
	rm ${BINDIR}/*.class

docs:
	javadoc -classpath ${BINDIR} -d ${DOCDIR} ${SRCDIR}/*.java

run:
	java -cp bin MedianFilterParallel

run2:
	java -cp bin MedianFilterSerial

run3:
	java -cp bin MeanFilterParallel

run4:
	java -cp bin MeanFilterSerial

cleandocs:
	rm -rf ${DOCDIR}/*
