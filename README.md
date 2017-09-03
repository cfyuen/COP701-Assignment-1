# COP701-Assignment-1 #

Sample command to start mininet
> sudo mn --custom=startall.py --topo=tree,2,3

Then in the CLI of mininet, you can type in
> startall

Now all nodes will start automatically and open up a new window.

To issue commands in a node
> Go to the corresponding window
> help (a list of commands should show up)

To add a node (here replace * with a number)
> xterm h*
In the command prompt, type
> java -jar ~/dev/A1.jar | tee /tmp/h*.log

To delete a node
> Go to the corresponding window
> Ctrl-C to the running node

-------------------
## Other simple commands ##

To see it is working,
> xterm h1

Then on the new terminal,
> nc -z -v -w5 10.0.0.2 42000

This command should show successful.
