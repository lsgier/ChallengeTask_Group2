#!/usr/bin/env bash

if [ $# -lt 1 ]; then
	echo "Usage: $0 mountpoint [bootstrapIP]"
	exit 1
fi

mountPoint="$1"

if [ -z "$1" ]
  then
    echo "Mount point is not specified"
    exit 1
fi

if [ "$(ls -A $1)" ]; then
   echo "Mount point is not empty"
   exit 1
fi

if [ -z "$2" ]
  then
     nohup java -cp p2pfs.jar -Xmx512M -XX:MaxDirectMemorySize=512M challengetask.group02.peer.FSPeer "$mountPoint" >peer.log &
  else
     nohup java -cp p2pfs.jar -Xmx512M -XX:MaxDirectMemorySize=512M challengetask.group02.peer.FSPeer "$mountPoint" "$2" >peer-additional.log &
fi