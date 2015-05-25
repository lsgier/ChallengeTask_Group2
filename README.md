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

## How it works ##

## Development Workflow ##
[Link to the board](https://trello.com/b/ylcjsnyd/challenge-task)
