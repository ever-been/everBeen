# Try the Intel compiler. Its warning messages are great and so is the Eclipse integration.
# CC=icc

CFLAGS=-std=c99 -O2 -pipe -Wall -pedantic

all: bcmd

bcmd: unix/bcmd.c
	$(CC) $(CFLAGS) -o target/$@ unix/$@.c

clean:
	rm bin/bcmd
