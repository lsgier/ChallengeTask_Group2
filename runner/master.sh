#!/usr/bin/env bash

mountPoint="$1"

rm -rf "$mountPoint/"*

if [ ! -d "$mountPoint/master" ]; then
	mkdir -p "$mountPoint/master"
fi

java -cp target/p2pfs.jar prototype.FSPeer "$mountPoint/master"
