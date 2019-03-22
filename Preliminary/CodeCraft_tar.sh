#!/bin/bash

SCRIPT=$(readlink -f "$0")
BASEDIR=$(dirname "$SCRIPT")
cd $BASEDIR

if [ ! -d code ] || [ ! -f makelist.txt ]
then
    echo "ERROR: $BASEDIR is not first valid directory of SDK_java for CodeCraft-2019."
    echo "  Please run this script in first regular directory of SDK_java."
    exit -1
fi

rm -f CodeCraft_code.tar.gz
tar -zcPf CodeCraft_code.tar.gz *