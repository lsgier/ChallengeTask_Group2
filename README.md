P2P FILE SYSTEM
===============
[Challenge task](http://www.csg.uzh.ch/teaching/fs15/p2p/challenge.html) for the course Overlay Networks, Decentralized Systems and Their Applications (UZH, Sptring 2015).

## Description ##
Opens Source distributed file system built on top of [TomP2P](https://github.com/tomp2p/TomP2P) and [FuseJNA](https://github.com/EtiennePerot/fuse-jna). TomP2P is "a library and a distributed hash table (DHT) implementation which provides a decentralized key-value infrastructure for distributed applications" and FuseJNA is a set of Java bindings to [FUSE library](http://fuse.sourceforge.net/)

## System requirements ##
Maven is needed to build jar.
Linux distribution running kernel 2.4.X+ and Java 6.
Also works on osX via [osxfuse](https://osxfuse.github.io/)

## How to run ##
Simply check out the code from repository and run `runner/mkjar.sh`. That will create the `p2pfs.jar` in `target` directory.
Further the jar can be executed as follows:

`java -jar p2pfs.jar mount-point [bootstrap-ip] [peer-number]`

`mount-point` have to be an empty directory,

`[bootstrap-ip]` is the ip of a master peer, if not defined then peer will start as a server

`[peer-number]` is used for local testing, when all peers share the same ip but listen to different ports.

Also, runner scripts can be used (located in `runner` directory):

`runner/standalone.sh mount-point [bootstrap-ip]` standard mode,

`runner/local-master.sh mount-point` to start a "server" and to run several additional peers:

`runner/local-additional.sh mount-point peer-number`.

In all scripts inside `runner` it is assumed that commands are run from the project root directory and that jar file is inside `target` directory. Simply removing `cd target` from runner scripts and copying them with the jar in the same directory will do the trick.

## How it works ##
The documentation for FuseJNA can be found on its github page, the particular details of TomP2P library could be known from numerous examples.

For more details, check wiki.

## Further development ##
There's a lot to improve. Performance issues are not resolved yet. 
In addition, it was never tested on more then 20 peers and only few reading and writing at the same time, and this can be an issue. 

Shutdown and reconnect routines have to be implemented.

## Development Workflow ##
[Link to the board](https://trello.com/b/ylcjsnyd/challenge-task)
