JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	http/server/WebServer.java \
	http/server/Logger.java \
	http/client/WebPing.java \


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) http/server/*.class
	$(RM) http/client/*.class
