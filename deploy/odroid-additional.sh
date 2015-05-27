#!/bin/bash

# List of ODroids"
IPS="192.168.1.110
192.168.1.105"

PRIVATE_KEY="odroid.priv"


for i in $IPS;
do
  echo "Execute $CMD on $i..."
  ssh -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "root@$i" "cd /root/group2 && ./run.sh mount 192.168.1.119"
done
