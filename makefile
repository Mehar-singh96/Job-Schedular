JCC = javac

JFLAGS = -g

default: jobscheduler.class

jobscheduler.class: jobscheduler.java
	$(JCC) $(JFLAGS) jobscheduler.java

clean:
	$(RM) *.class
