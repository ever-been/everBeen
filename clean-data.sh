#!/bin/sh
rm -rf data/softwarerepository
rm -rf data/hostruntime/cache/*.bpk
rm -rf data/hostruntime/tasks/*
rm -rf data/hostruntime/boot/*.bpk
mkdir -p data/hostruntime/tasks/system
