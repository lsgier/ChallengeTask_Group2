#!/usr/bin/env bash

mountPoint="$1"
port="400$2"

if [ $# -lt 2 ]; then
	echo "Usage: $0 mountpoint peerNumber" >&2
	exit 1
fi

re='^[0-9]+$'
if ! [[ $2 =~ $re ]] ; then
   echo "error: peerNumber is not an integer" >&2; exit 1
fi

if [ $2 -eq 0 ]; then
   echo "error: peerNumber can not be 0, start with 1";
   exit 1;
fi

if [ -d "$mountPoint/peer$2" ]; then
	echo "error: peer with number $2 already exists or master is not running" >&2
	exit 1
fi

if [ ! -d "$mountPoint/peer$2" ]; then
	mkdir -p "$mountPoint/peer$2"
fi

cd "target"
java -jar -Xmx512M -XX:MaxDirectMemorySize=512M ../target/p2pfs.jar  "$mountPoint/peer$2" "127.0.0.1" "$port"
