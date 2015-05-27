#!/bin/bash

# List of ODroids"
IPS="192.168.1.119"

PRIVATE_KEY="odroid.priv"



  ssh -o "StrictHostKeyChecking no" -i "$PRIVATE_KEY" "root@192.168.1.119" "cd /root/group2 && ./run.sh mount"

