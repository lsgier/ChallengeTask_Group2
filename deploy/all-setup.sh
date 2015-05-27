#!/bin/bash

# List of ODroids"
IPS="192.168.1.119
192.168.1.107
192.168.1.146
192.168.1.109
192.168.1.115
192.168.1.105
192.168.1.110
192.168.1.114
192.168.1.101
192.168.1.113
192.168.1.107
192.168.1.108
192.168.1.112"

PRIVATE_KEY="odroid.priv"


for i in $IPS;
do
  echo "Uploading cleaning script to $i"
  scp -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "group2-clean-all.sh" "root@$i:/root/group2-clean-all.sh"
  ssh -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "root@$i" "chmod a+x /root/group2-clean-all.sh"
  ssh -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "root@$i" "/root/group2-clean-all.sh"
  ssh -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "root@$i" "rm /root/group2-clean-all.sh"
  ssh -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "root@$i" "mkdir /root/group2 && mkdir /root/group2/mount"
  scp -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "p2pfs.jar" "root@$i:/root/group2/p2pfs.jar"
  scp -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "run.sh" "root@$i:/root/group2/run.sh"	
done

