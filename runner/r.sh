#!/usr/bin/env bash

set -e

if [ $# -lt 2 ]; then
	echo "Usage: $0 full.class.name [fs-arguments] mountpoint" >&2
	exit 1
fi

if ! which mvn &> /dev/null; then
  echo 'maven not found in $PATH. Please install maven.' >&2
  exit 1
fi

mountPoint="${@: -1}"
if [ ! -d "$mountPoint" ]; then
	mkdir -p "$mountPoint"
fi
absoluteMountpoint="$(cd "$mountPoint" && pwd)"
set -- "${@:1:$(expr "$#" - 1)}" "$absoluteMountpoint"

cd "$(dirname "$BASH_SOURCE")/.."



#mvn clean compile assembly:single
java -cp target/p2pfs.jar $@

