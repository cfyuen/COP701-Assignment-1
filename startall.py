from mininet.cli import CLI
import random
import string
import time

def startall( self, line ):
    "mycmd is an example command to extend the Mininet CLI"
    net = self.mn
    for h in net.hosts:
        account = ''.join(random.choice('0123') for _ in range(8))
        print "Start java in " , h , " with account name " , account
        h.sendCmd("java -jar ~/dev/A1.jar " , account)
        time.sleep(0.5)
CLI.do_startall = startall
