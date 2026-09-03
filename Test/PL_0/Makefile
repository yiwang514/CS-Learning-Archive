CC ?= gcc
CFLAGS ?= -std=c11 -Wall -Wextra -Iinclude
PROGRAM := bin/pl0.exe
SRC := src/pl0.c

all: $(PROGRAM)

$(PROGRAM): $(SRC) include/pl0.h
	@if not exist bin mkdir bin
	$(CC) $(CFLAGS) -o $@ $(SRC)

run: $(PROGRAM)
	$(PROGRAM) examples/multiply.pl0

clean:
	@if exist $(PROGRAM) del $(PROGRAM)

.PHONY: all run clean

