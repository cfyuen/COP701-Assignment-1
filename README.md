# COP701-Assignment-1

Sample command to start mininet
> sudo mn --custom=startall.py --topo=tree,2,3

Then in the CLI of mininet, you can type in
> startall

To see it is working,
> xterm h1

Then on the new terminal,
> nc -z -v -w5 10.0.0.2 42000

This command should show successful.
