#!/bin/bash

export HBASE_PATH=/home/finaxys/hbase-1.1.2

$HBASE_PATH/bin/hbase shell < ./delete-table.hb
$HBASE_PATH/bin/hbase shell < ./create-table.hb
