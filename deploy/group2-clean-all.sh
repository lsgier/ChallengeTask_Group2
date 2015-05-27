#!/bin/bash

kill -9 `ps -ef | grep p2pfs.jar | grep -v grep | awk '{print $2}'`
umount /root/group2/mount
rm -rf /root/group2

