/*
 * PL/0 lexical analyzer for the C language case in
 * "Compilation Principles (3rd Edition)".
 *
 * This header keeps the structure and naming style of the textbook:
 * symbol enumeration, character state, reserved-word tables, and
 * the getch()/getsym() interfaces.
 */
#ifndef PL0_TEXTBOOK_H
#define PL0_TEXTBOOK_H

#include <stdio.h>

#define norw 13
#define nmax 14
#define al 10
#define symnum 32

enum symbol {
    nul,       ident,     number,    plus,      minus,
    times,     slash,     oddsym,    eql,       neq,
    lss,       leq,       gtr,       geq,       lparen,
    rparen,    comma,     semicolon, period,    becomes,
    beginsym,  endsym,    ifsym,     thensym,   whilesym,
    writesym,  readsym,   dosym,     callsym,   constsym,
    varsym,    procsym
};

extern FILE *fin;
extern char ch;
extern enum symbol sym;
extern char id[al + 1];
extern int num;
extern int cc;
extern int ll;
extern int cx;
extern char line[81];
extern char a[al + 1];
extern char word[norw][al];
extern enum symbol wsym[norw];
extern enum symbol ssym[256];
extern int err;

void error(int n);
int getch(void);
int getsym(void);
void init(void);
void print_tokens(void);

#endif

