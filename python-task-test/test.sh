#!/bin/bash

echo "error" | python logger.py -e
echo "warn"  | python logger.py -w
echo "info"  | python logger.py -i
echo "debug" | python logger.py -d
echo "trace" | python logger.py -t
