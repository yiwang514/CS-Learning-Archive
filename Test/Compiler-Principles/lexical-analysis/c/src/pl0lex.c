/*
 * Standalone lexical analyzer for PL/0.
 * Usage: pl0lex [source-file]
 * Without a file name it reads from standard input.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ID_LEN 10
#define MAX_DIGITS 14
#define MAX_TEXT 128

typedef enum {
    TOK_IDENTIFIER,
    TOK_INTEGER,
    TOK_KEYWORD,
    TOK_SPECIAL,
    TOK_ERROR,
    TOK_EOF
} TokenKind;

typedef struct {
    TokenKind kind;
    char lexeme[MAX_TEXT];
    char message[MAX_TEXT];
    int line;
    int column;
} Token;

static const char *keywords[] = {
    "begin", "call", "const", "do", "end", "if", "odd",
    "procedure", "read", "then", "var", "while", "write"
};

static const char *source;
static size_t source_len;
static size_t pos;
static int line = 1;
static size_t line_start;
static int lexical_errors;

static int is_digit_char(int c)
{
    return c >= '0' && c <= '9';
}

static int is_letter_char(int c)
{
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
}

static int is_alnum_char(int c)
{
    return is_letter_char(c) || is_digit_char(c);
}

static char to_lower_ascii(char c)
{
    if (c >= 'A' && c <= 'Z') {
        return (char)(c - 'A' + 'a');
    }
    return c;
}

static int is_keyword_at(size_t start, size_t end)
{
    size_t i;

    for (i = 0; i < sizeof(keywords) / sizeof(keywords[0]); i++) {
        size_t length = strlen(keywords[i]);
        size_t j;

        if (end - start != length) {
            continue;
        }
        for (j = 0; j < length; j++) {
            if (to_lower_ascii(source[start + j]) != keywords[i][j]) {
                break;
            }
        }
        if (j == length) {
            return 1;
        }
    }
    return 0;
}

static int current_char(void)
{
    return pos < source_len ? (unsigned char)source[pos] : EOF;
}

static int current_column(void)
{
    return (int)(pos - line_start) + 1;
}

static void advance(void)
{
    if (pos >= source_len) {
        return;
    }
    if (source[pos] == '\n') {
        line++;
        line_start = pos + 1;
    }
    pos++;
}

static void report_error(int error_line, int error_column, const char *message)
{
    printf("%4d %4d  ERROR       %s\n", error_line, error_column, message);
    lexical_errors++;
}

static int skip_comment(void)
{
    int start_line = line;
    int start_column = current_column();

    advance(); /* '/' */
    advance(); /* '*' */

    while (1) {
        if (pos >= source_len) {
            report_error(start_line, start_column, "unterminated comment");
            return -1;
        }
        if (current_char() == '/' && pos + 1 < source_len &&
            source[pos + 1] == '*') {
            advance();
            advance();
        } else if (current_char() == '*' && pos + 1 < source_len &&
                   source[pos + 1] == '/') {
            advance();
            advance();
            return 0;
        } else {
            advance();
        }
    }
}

static int skip_ignored(void)
{
    while (1) {
        int c = current_char();
        while (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
            advance();
            c = current_char();
        }
        if (c == '/' && pos + 1 < source_len && source[pos + 1] == '*') {
            if (skip_comment() < 0) {
                return -1;
            }
            continue;
        }
        return 0;
    }
}

static void copy_lexeme(char *dest, size_t dest_size, size_t start, size_t end)
{
    size_t length = end - start;
    if (length >= dest_size) {
        length = dest_size - 1;
    }
    memcpy(dest, source + start, length);
    dest[length] = '\0';
}

static Token make_token(TokenKind kind, int token_line, int token_column,
                        size_t start, size_t end)
{
    Token token;
    memset(&token, 0, sizeof(token));
    token.kind = kind;
    token.line = token_line;
    token.column = token_column;
    copy_lexeme(token.lexeme, sizeof(token.lexeme), start, end);
    return token;
}

static Token make_error_token(int token_line, int token_column,
                              size_t start, size_t end, const char *message)
{
    Token token = make_token(TOK_ERROR, token_line, token_column, start, end);
    snprintf(token.message, sizeof(token.message), "%s", message);
    return token;
}

static Token scan_word(int token_line, int token_column, size_t start)
{
    while (is_alnum_char(current_char())) {
        advance();
    }

    if (pos - start > MAX_ID_LEN) {
        return make_error_token(token_line, token_column, start, pos,
                                "identifier is longer than 10 characters");
    }

    if (is_keyword_at(start, pos)) {
        return make_token(TOK_KEYWORD, token_line, token_column, start, pos);
    }

    return make_token(TOK_IDENTIFIER, token_line, token_column, start, pos);
}

static Token scan_number(int token_line, int token_column, size_t start)
{
    size_t digit_count;
    Token token;

    while (is_digit_char(current_char())) {
        advance();
    }
    digit_count = pos - start;

    if (digit_count > MAX_DIGITS) {
        while (is_alnum_char(current_char())) {
            advance();
        }
        return make_error_token(token_line, token_column, start, pos,
                                "integer constant has more than 14 digits");
    }

    if (is_letter_char(current_char())) {
        while (is_alnum_char(current_char())) {
            advance();
        }
        return make_error_token(token_line, token_column, start, pos,
                                "number cannot be followed by a letter");
    }

    token = make_token(TOK_INTEGER, token_line, token_column, start, pos);
    return token;
}

static Token scan_special(int token_line, int token_column, size_t start)
{
    int c = current_char();

    advance();

    if (c == '+') {
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }
    if (c == '-') {
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }
    if (c == '*') {
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }
    if (c == '/') {
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }
    if (c == '(' || c == ')' || c == '=' || c == ',' ||
        c == '.' || c == ';') {
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }
    if (c == ':') {
        if (current_char() == '=') {
            advance();
            return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
        }
        return make_error_token(token_line, token_column, start, pos,
                                "':' must be followed by '='");
    }
    if (c == '<') {
        if (current_char() == '=' || current_char() == '>') {
            advance();
        }
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }
    if (c == '>') {
        if (current_char() == '=') {
            advance();
        }
        return make_token(TOK_SPECIAL, token_line, token_column, start, pos);
    }

    if (c >= 32 && c < 127) {
        char message[MAX_TEXT];
        snprintf(message, sizeof(message), "illegal character '%c'", c);
        return make_error_token(token_line, token_column, start, pos, message);
    }
    {
        char message[MAX_TEXT];
        snprintf(message, sizeof(message), "illegal character with code %d", c);
        return make_error_token(token_line, token_column, start, pos, message);
    }
}

static Token next_token(void)
{
    int token_line;
    int token_column;
    size_t start;
    int c;

    if (skip_ignored() < 0) {
        Token eof_token = make_token(TOK_EOF, line, current_column(), pos, pos);
        return eof_token;
    }

    c = current_char();
    if (c == EOF) {
        return make_token(TOK_EOF, line, current_column(), pos, pos);
    }

    token_line = line;
    token_column = current_column();
    start = pos;

    if (is_letter_char(c)) {
        return scan_word(token_line, token_column, start);
    }
    if (is_digit_char(c)) {
        return scan_number(token_line, token_column, start);
    }
    return scan_special(token_line, token_column, start);
}

static char *read_all(FILE *input, size_t *length)
{
    size_t capacity = 1024;
    size_t size = 0;
    char *buffer = (char *)malloc(capacity);
    int c;

    if (buffer == NULL) {
        return NULL;
    }

    while ((c = fgetc(input)) != EOF) {
        if (size + 1 >= capacity) {
            char *larger;
            capacity *= 2;
            larger = (char *)realloc(buffer, capacity);
            if (larger == NULL) {
                free(buffer);
                return NULL;
            }
            buffer = larger;
        }
        buffer[size++] = (char)c;
    }
    buffer[size] = '\0';
    *length = size;
    return buffer;
}

static const char *kind_name(TokenKind kind)
{
    switch (kind) {
    case TOK_IDENTIFIER:
        return "IDENTIFIER";
    case TOK_INTEGER:
        return "INTEGER";
    case TOK_KEYWORD:
        return "KEYWORD";
    case TOK_SPECIAL:
        return "SPECIAL";
    case TOK_ERROR:
        return "ERROR";
    case TOK_EOF:
        return "EOF";
    }
    return "?";
}

static void print_token(const Token *token)
{
    printf("%4d %4d  %-10s ", token->line, token->column,
           kind_name(token->kind));

    switch (token->kind) {
    case TOK_IDENTIFIER:
        printf("(identifier, \"%s\")\n", token->lexeme);
        break;
    case TOK_INTEGER:
        printf("(integer, \"%s\")\n", token->lexeme);
        break;
    case TOK_KEYWORD:
        printf("(keyword, \"%s\")\n", token->lexeme);
        break;
    case TOK_SPECIAL:
        printf("(special, \"%s\")\n", token->lexeme);
        break;
    case TOK_ERROR:
        printf("%s \"%s\"\n", token->message, token->lexeme);
        break;
    case TOK_EOF:
        printf("(eof, -)\n");
        break;
    }
}

int main(int argc, char **argv)
{
    FILE *input;
    char *buffer;
    size_t length;
    int token_count = 0;
    Token token;

    if (argc > 2) {
        fprintf(stderr, "usage: pl0lex [source-file]\n");
        return 2;
    }

    input = stdin;
    if (argc == 2) {
        input = fopen(argv[1], "rb");
        if (input == NULL) {
            fprintf(stderr, "cannot open file: %s\n", argv[1]);
            return 2;
        }
    }

    buffer = read_all(input, &length);
    if (argc == 2) {
        fclose(input);
    }
    if (buffer == NULL) {
        fprintf(stderr, "cannot read input\n");
        return 2;
    }

    source = buffer;
    source_len = length;

    printf("%4s %4s  %-10s %s\n",
           "LINE", "COL", "CATEGORY", "TOKEN FORM");

    do {
        token = next_token();
        print_token(&token);
        if (token.kind == TOK_ERROR) {
            lexical_errors++;
        }
        if (token.kind != TOK_EOF) {
            token_count++;
        }
    } while (token.kind != TOK_EOF);

    printf("\nsummary: %d token(s), %d lexical error(s)\n",
           token_count, lexical_errors);

    free(buffer);
    return lexical_errors == 0 ? 0 : 1;
}
