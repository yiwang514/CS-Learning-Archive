#include <stdio.h>
#include <string.h>

#include "pl0_t.h"

#define max_tokens 2048
#define token_text_size 128

struct token_record {
    char word[token_text_size];
    char category[token_text_size];
    char value[token_text_size];
};

FILE *fin;
char ch;
enum symbol sym;
char id[al + 1];
int num;
int cc;
int ll;
int cx;
char line[81];
char a[al + 1];
char word[norw][al];
enum symbol wsym[norw];
enum symbol ssym[256];
int err;

static struct token_record tokens[max_tokens];
static int token_count;
static int input_finished;
static char number_text[64];

static const char *symbol_name[] = {
    "NUL",      "IDENT",    "NUMBER",   "PLUS",     "MINUS",
    "TIMES",    "SLASH",    "ODDSYM",   "EQL",      "NEQ",
    "LSS",      "LEQ",      "GTR",      "GEQ",      "LPAREN",
    "RPAREN",   "COMMA",    "SEMICOLON","PERIOD",   "BECOMES",
    "BEGINSYM", "ENDSYM",   "IFSYM",    "THENSYM",  "WHILESYM",
    "WRITESYM", "READSYM",  "DOSYM",    "CALLSYM",  "CONSTSYM",
    "VARSYM",   "PROCSYM"
};

static const char *symbol_text[] = {
    "",     "",      "",      "+",    "-",
    "*",    "/",     "",      "=",    "#",
    "<",    "<=",    ">",     ">=",   "(",
    ")",    ",",     ";",     ".",    ":=",
    "begin","end",   "if",    "then", "while",
    "write","read",  "do",    "call", "const",
    "var",  "procedure"
};

static int is_keyword_symbol(void)
{
    return sym == oddsym || (sym >= beginsym && sym <= procsym);
}

static void copy_text(char *dest, size_t size, const char *source)
{
    if (size == 0) {
        return;
    }
    strncpy(dest, source, size - 1);
    dest[size - 1] = '\0';
}

static void add_token(const char *word_text, const char *category,
                      const char *value)
{
    if (token_count >= max_tokens) {
        return;
    }

    copy_text(tokens[token_count].word,
              sizeof(tokens[token_count].word), word_text);
    copy_text(tokens[token_count].category,
              sizeof(tokens[token_count].category), category);
    copy_text(tokens[token_count].value,
              sizeof(tokens[token_count].value), value);
    token_count++;
}

static void record_error_token(const char *text, const char *message)
{
    add_token(text, "ERROR", message);
}

static void record_current_token(void)
{
    if (sym == ident) {
        add_token(id, "IDENTIFIER", id);
    } else if (sym == number) {
        add_token(number_text, "INTEGER", number_text);
    } else if (is_keyword_symbol()) {
        add_token(id, "KEYWORD", symbol_name[sym]);
    } else if (sym >= plus && sym <= becomes && sym != oddsym) {
        add_token(symbol_text[sym], "SPECIAL", symbol_name[sym]);
    } else if (sym == nul) {
        record_error_token("NUL", "unrecognized symbol");
    }
}


void error(int n)
{
    char space[81];
    int i;

    for (i = 0; i < 80; i++) {
        space[i] = ' ';
    }
    space[80] = '\0';

    if (cc > 0 && cc <= 80) {
        space[cc - 1] = '\0';
    }

    printf("****%s!%d\n", space, n);
    err++;
}


int getch(void)
{
    if (cc == ll) {
        if (input_finished) {
            printf("program incomplete\n");
            return -1;
        }

        ll = 0;
        cc = 0;
        printf("%d ", cx);

        while (1) {
            int c = fgetc(fin);
            if (c == EOF) {
                input_finished = 1;
                break;
            }
            ch = (char)c;
            printf("%c", ch);
            if (ll < 80) {
                line[ll++] = ch;
            }
            if (ch == '\n') {
                break;
            }
        }

        if (ll == 0 && input_finished) {
            printf("\nprogram incomplete\n");
            return -1;
        }

        if (ll < 80) {
            line[ll++] = ' ';
        }
        printf("\n");
    }

    ch = line[cc];
    cc++;
    return 0;
}


void init(void)
{
    int i;

    for (i = 0; i < 256; i++) {
        ssym[i] = nul;
    }

    ssym['+'] = plus;
    ssym['-'] = minus;
    ssym['*'] = times;
    ssym['/'] = slash;
    ssym['('] = lparen;
    ssym[')'] = rparen;
    ssym['='] = eql;
    ssym[','] = comma;
    ssym['.'] = period;
    ssym['#'] = neq;
    ssym[';'] = semicolon;

    strcpy(word[0], "begin");
    strcpy(word[1], "call");
    strcpy(word[2], "const");
    strcpy(word[3], "do");
    strcpy(word[4], "end");
    strcpy(word[5], "if");
    strcpy(word[6], "odd");
    strcpy(word[7], "procedure");
    strcpy(word[8], "read");
    strcpy(word[9], "then");
    strcpy(word[10], "var");
    strcpy(word[11], "while");
    strcpy(word[12], "write");

    wsym[0] = beginsym;
    wsym[1] = callsym;
    wsym[2] = constsym;
    wsym[3] = dosym;
    wsym[4] = endsym;
    wsym[5] = ifsym;
    wsym[6] = oddsym;
    wsym[7] = procsym;
    wsym[8] = readsym;
    wsym[9] = thensym;
    wsym[10] = varsym;
    wsym[11] = whilesym;
    wsym[12] = writesym;

    token_count = 0;
    input_finished = 0;
}


int getsym(void)
{
    int i;
    int j;
    int k;
    int identifier_too_long;

    while (1) {
        while (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
            if (getch() == -1) {
                return -1;
            }
        }

        if (ch == '/' ) {
            if (getch() == -1) {
                return -1;
            }
            if (ch == '*') {
                if (getch() == -1) {
                    return -1;
                }
                while (1) {
                    if (ch == '*') {
                        if (getch() == -1) {
                            error(38);
                            return -1;
                        }
                        if (ch == '/') {
                            if (getch() == -1) {
                                return -1;
                            }
                            break;
                        }
                    } else if (getch() == -1) {
                        error(38);
                        return -1;
                    }
                }
                continue;
            }

            sym = slash;
            record_current_token();
            return 0;
        }
        break;
    }

    if (ch >= 'a' && ch <= 'z') {
        k = 0;
        identifier_too_long = 0;
        do {
            if (k < al) {
                a[k] = ch;
                k++;
            } else {
                identifier_too_long = 1;
            }
            if (getch() == -1) {
                return -1;
            }
        } while ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9'));

        a[k] = '\0';
        strcpy(id, a);
        if (identifier_too_long) {
            error(36);
            record_error_token(id, "identifier is longer than 10 characters");
        }

        i = 0;
        j = norw - 1;
        do {
            k = (i + j) / 2;
            if (strcmp(id, word[k]) <= 0) {
                j = k - 1;
            }
            if (strcmp(id, word[k]) >= 0) {
                i = k + 1;
            }
        } while (i <= j);

        if (i - 1 > j) {
            sym = wsym[k];
        } else {
            sym = ident;
        }
    } else if (ch >= '0' && ch <= '9') {
        k = 0;
        i = 0;
        num = 0;
        sym = number;
        do {
            if (i < (int)sizeof(number_text) - 1) {
                number_text[i] = ch;
                i++;
            }
            num = 10 * num + ch - '0';
            k++;
            if (getch() == -1) {
                return -1;
            }
        } while (ch >= '0' && ch <= '9');
        number_text[i] = '\0';

        k--;
        if (k > nmax) {
            error(30);
        }
        if (ch >= 'a' && ch <= 'z') {
            error(39);
            record_error_token("", "number cannot be followed by a letter");
        }
    } else if (ch == ':') {
        if (getch() == -1) {
            return -1;
        }
        if (ch == '=') {
            sym = becomes;
            if (getch() == -1) {
                return -1;
            }
        } else {
            sym = nul;
            error(19);
            record_error_token(":", "':' must be followed by '='");
            return 0;
        }
    } else if (ch == '<') {
        if (getch() == -1) {
            return -1;
        }
        if (ch == '=') {
            sym = leq;
            if (getch() == -1) {
                return -1;
            }
        } else {
            sym = lss;
        }
    } else if (ch == '>') {
        if (getch() == -1) {
            return -1;
        }
        if (ch == '=') {
            sym = geq;
            if (getch() == -1) {
                return -1;
            }
        } else {
            sym = gtr;
        }
    } else {
        sym = ssym[(unsigned char)ch];
        if (sym == nul) {
            char invalid[4];
            invalid[0] = ch;
            invalid[1] = '\0';
            error(37);
            record_error_token(invalid, "illegal character");
            if (getch() == -1) {
                return -1;
            }
            return 0;
        }
        if (sym != period) {
            if (getch() == -1) {
                return -1;
            }
        }
    }

    record_current_token();
    return 0;
}

void print_tokens(void)
{
    int i;

    printf("\nWord                Category            Value\n");
    for (i = 0; i < token_count; i++) {
        printf("%-20s%-20s%-20s\n",
               tokens[i].word, tokens[i].category, tokens[i].value);
    }
}

int main(int argc, char **argv)
{
    char fname[256];

    printf("Input pl/0 file?   ");
    if (argc > 1) {
        strncpy(fname, argv[1], sizeof(fname) - 1);
        fname[sizeof(fname) - 1] = '\0';
        printf("%s\n", fname);
    } else {
        if (scanf("%255s", fname) != 1) {
            printf("\n");
            return 1;
        }
    }

    fin = fopen(fname, "r");
    if (fin == NULL) {
        printf("Can't open file!\n");
        return 1;
    }

    init();
    cc = 0;
    cx = 0;
    ll = 0;
    err = 0;
    ch = ' ';

    while (sym != period) {
        if (getsym() == -1) {
            break;
        }
    }

    print_tokens();
    printf("\n");
    fclose(fin);
    return err == 0 ? 0 : 1;
}
