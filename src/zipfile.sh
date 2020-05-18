#!/bin/bash
tmp=`pwd`
cd $1
echo $2
echo $3
echo "zip $2 -r $3 -x '*/__pycache__*'"
zip $2 -r $3 -x '*/__pycache__*'
cd $tmp