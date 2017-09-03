from mininet.cli import CLI
from mininet.term import makeTerm
import time
import random

def startall( self, line ):
	"mycmd is an example command to extend the Mininet CLI"
	net = self.mn
	for h in net.hosts:
		if h.name == "h1" or random.random() < 0.8:
			print "Start java in " , h
			cmdLine = "java -jar ~/dev/A1.jar | tee /tmp/" + h.name + ".log"
			print "  Calling command: " , cmdLine
			# Running in foreground
			net.terms += makeTerm(h, cmd=cmdLine)
			# Running in background
			# h.sendCmd(cmdLine)
			time.sleep(2)
CLI.do_startall = startall
