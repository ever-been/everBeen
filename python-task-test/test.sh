#!/bin/bash

echo "error" | python $logger -e
echo "warn"  | python $logger -w
echo "info"  | python $logger -i
echo "debug" | python $logger -d
echo "trace" | python $logger -t
