import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PL/0 词法分析器（Java 版）。
 *
 * 整体流程：
 * main -> run -> getsym -> getch
 *
 * getch 负责从源文件读入一个字符，getsym 负责识别一个完整的单词。
 */
public class Pl0Lexer {
    private static final int MAX_IDENTIFIER_LENGTH = 10;
    private static final int MAX_NUMBER_LENGTH = 14;
    private static final int ERROR_LINE_WIDTH = 80;

    /** 与教材中的 enum symbol 对应。 */
    private enum Symbol {
        NUL, IDENT, NUMBER, PLUS, MINUS,
        TIMES, SLASH, ODDSYM, EQL, NEQ,
        LSS, LEQ, GTR, GEQ, LPAREN,
        RPAREN, COMMA, SEMICOLON, PERIOD, BECOMES,
        BEGINSYM, ENDSYM, IFSYM, THENSYM, WHILESYM,
        WRITESYM, READSYM, DOSYM, CALLSYM, CONSTSYM,
        VARSYM, PROCSYM
    }

    /** Token 表第二列使用的分类名称。 */
    private enum TokenCategory {
        IDENTIFIER, INTEGER, KEYWORD, SPECIAL, ERROR
    }

    /** Token 表中的一行。 */
    private static final class Token {
        private final String word;
        private final TokenCategory category;
        private final String value;

        private Token(String word, TokenCategory category, String value) {
            this.word = word;
            this.category = category;
            this.value = value;
        }
    }

    private static final Set<Symbol> KEYWORD_SYMBOLS = EnumSet.of(
            Symbol.ODDSYM,
            Symbol.BEGINSYM,
            Symbol.ENDSYM,
            Symbol.IFSYM,
            Symbol.THENSYM,
            Symbol.WHILESYM,
            Symbol.WRITESYM,
            Symbol.READSYM,
            Symbol.DOSYM,
            Symbol.CALLSYM,
            Symbol.CONSTSYM,
            Symbol.VARSYM,
            Symbol.PROCSYM
    );

    private static final Set<Symbol> SPECIAL_SYMBOLS = EnumSet.of(
            Symbol.PLUS,
            Symbol.MINUS,
            Symbol.TIMES,
            Symbol.SLASH,
            Symbol.EQL,
            Symbol.NEQ,
            Symbol.LSS,
            Symbol.LEQ,
            Symbol.GTR,
            Symbol.GEQ,
            Symbol.LPAREN,
            Symbol.RPAREN,
            Symbol.COMMA,
            Symbol.SEMICOLON,
            Symbol.PERIOD,
            Symbol.BECOMES
    );

    private final Map<String, Symbol> keywords = new LinkedHashMap<>();
    private final Map<Character, Symbol> specialCharacters = new LinkedHashMap<>();
    private final List<Token> tokens = new ArrayList<>();

    private BufferedReader reader;
    private String currentLine = "";
    private int currentColumn;
    private char currentChar = ' ';
    private Symbol currentSymbol = Symbol.NUL;
    private String identifier = "";
    private String numberText = "";
    private int codeAddress;
    private int errorCount;
    private boolean inputFinished;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Usage: java Pl0Lexer [source.pl0]");
            return;
        }

        System.out.print("Input pl/0 file?   ");
        String fileName = args.length == 1
                ? args[0]
                : readFileNameFromKeyboard();

        if (fileName == null) {
            return;
        }
        if (args.length == 1) {
            System.out.println(fileName);
        }

        Pl0Lexer lexer = new Pl0Lexer();
        int exitCode = lexer.run(fileName);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /** 在命令行没有给出文件名时，从键盘读取文件名。 */
    private static String readFileNameFromKeyboard() {
        try {
            BufferedReader keyboard = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String fileName = keyboard.readLine();
            if (fileName == null) {
                return null;
            }
            fileName = fileName.trim();
            return fileName.isEmpty() ? null : fileName;
        } catch (IOException exception) {
            System.out.println("Can't read the file name!");
            return null;
        }
    }

    /** 打开源文件，反复识别 Token，最后打印结果表。 */
    private int run(String fileName) {
        try (BufferedReader fileReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            reader = fileReader;
            init();

            while (currentSymbol != Symbol.PERIOD && getsym()) {
                // getsym() 已经记录了一个 Token，这里继续读取下一个 Token。
            }

            printTokens();
            System.out.print('\n');
            return errorCount == 0 ? 0 : 1;
        } catch (FileNotFoundException exception) {
            System.out.println("Can't open file!");
            return 1;
        } catch (IOException exception) {
            System.out.println("Read file failed: " + exception.getMessage());
            return 1;
        }
    }

    /** 初始化关键字表、特殊字符表以及运行状态。 */
    private void init() {
        keywords.clear();
        keywords.put("begin", Symbol.BEGINSYM);
        keywords.put("call", Symbol.CALLSYM);
        keywords.put("const", Symbol.CONSTSYM);
        keywords.put("do", Symbol.DOSYM);
        keywords.put("end", Symbol.ENDSYM);
        keywords.put("if", Symbol.IFSYM);
        keywords.put("odd", Symbol.ODDSYM);
        keywords.put("procedure", Symbol.PROCSYM);
        keywords.put("read", Symbol.READSYM);
        keywords.put("then", Symbol.THENSYM);
        keywords.put("var", Symbol.VARSYM);
        keywords.put("while", Symbol.WHILESYM);
        keywords.put("write", Symbol.WRITESYM);

        specialCharacters.clear();
        specialCharacters.put('+', Symbol.PLUS);
        specialCharacters.put('-', Symbol.MINUS);
        specialCharacters.put('*', Symbol.TIMES);
        specialCharacters.put('/', Symbol.SLASH);
        specialCharacters.put('(', Symbol.LPAREN);
        specialCharacters.put(')', Symbol.RPAREN);
        specialCharacters.put('=', Symbol.EQL);
        specialCharacters.put(',', Symbol.COMMA);
        specialCharacters.put('.', Symbol.PERIOD);
        specialCharacters.put('#', Symbol.NEQ);
        specialCharacters.put(';', Symbol.SEMICOLON);

        currentLine = "";
        currentColumn = 0;
        currentChar = ' ';
        currentSymbol = Symbol.NUL;
        identifier = "";
        numberText = "";
        codeAddress = 0;
        errorCount = 0;
        inputFinished = false;
        tokens.clear();
    }

    /**
     * 读取一个字符。当前行读完后，会自动读取下一行。
     *
     * @return 0 表示成功，-1 表示源文件已经结束
     */
    private int getch() {
        if (currentColumn >= currentLine.length()) {
            if (!readNextLine()) {
                return -1;
            }
        }

        currentChar = currentLine.charAt(currentColumn);
        currentColumn++;
        return 0;
    }

    /** 读取并回显下一行；每一行末尾补一个空格，方便识别文件结尾。 */
    private boolean readNextLine() {
        currentColumn = 0;
        currentLine = "";

        if (inputFinished) {
            System.out.print("program incomplete\n");
            return false;
        }

        String line;
        try {
            line = reader.readLine();
        } catch (IOException exception) {
            System.out.print("Read file failed: " + exception.getMessage() + "\n");
            inputFinished = true;
            return false;
        }

        if (line == null) {
            inputFinished = true;
            System.out.print(codeAddress + " \nprogram incomplete\n");
            return false;
        }

        currentLine = line + ' ';
        System.out.print(codeAddress + " " + line + "\n\n");
        return true;
    }

    /**
     * 识别一个单词，并把结果保存到 currentSymbol 或 tokens 中。
     *
     * @return true 表示获得了一个 Token，false 表示源文件结束
     */
    private boolean getsym() {
        while (true) {
            skipWhitespace();
            if (inputFinished) {
                return false;
            }

            if (currentChar != '/') {
                break;
            }

            if (getch() == -1) {
                return false;
            }
            if (currentChar != '*') {
                currentSymbol = Symbol.SLASH;
                recordCurrentToken();
                return true;
            }

            if (!skipComment()) {
                return false;
            }
        }

        boolean tokenReady;
        if (isLowercaseLetter(currentChar)) {
            readIdentifier();
            tokenReady = true;
        } else if (isDigit(currentChar)) {
            readNumber();
            tokenReady = true;
        } else if (currentChar == ':') {
            tokenReady = readBecomes();
        } else if (currentChar == '<') {
            tokenReady = readLessOperator();
        } else if (currentChar == '>') {
            tokenReady = readGreaterOperator();
        } else {
            tokenReady = readSingleSpecialCharacter();
        }

        if (tokenReady) {
            recordCurrentToken();
        }
        return tokenReady || !inputFinished;
    }

    /** 跳过空格、换行、回车和制表符。 */
    private void skipWhitespace() {
        while (currentChar == ' ' || currentChar == '\n'
                || currentChar == '\r' || currentChar == '\t') {
            if (getch() == -1) {
                return;
            }
        }
    }

    /** 跳过一条从斜杠星号开始、到星号斜杠结束的注释。 */
    private boolean skipComment() {
        if (getch() == -1) {
            error(38);
            return false;
        }

        while (true) {
            if (currentChar == '*') {
                if (getch() == -1) {
                    error(38);
                    return false;
                }
                if (currentChar == '/') {
                    if (getch() == -1) {
                        return false;
                    }
                    return true;
                }
            } else if (getch() == -1) {
                error(38);
                return false;
            }
        }
    }

    /** 识别标识符或关键字。 */
    private void readIdentifier() {
        StringBuilder fullIdentifier = new StringBuilder();

        do {
            fullIdentifier.append(currentChar);
            if (getch() == -1) {
                break;
            }
        } while (isLowercaseLetter(currentChar) || isDigit(currentChar));

        boolean identifierTooLong = fullIdentifier.length() > MAX_IDENTIFIER_LENGTH;
        if (identifierTooLong) {
            fullIdentifier.setLength(MAX_IDENTIFIER_LENGTH);
        }

        identifier = fullIdentifier.toString();
        if (identifierTooLong) {
            error(36);
            recordErrorToken(identifier, "identifier is longer than 10 characters");
        }

        currentSymbol = keywords.getOrDefault(identifier, Symbol.IDENT);
    }

    /** 识别整数。 */
    private void readNumber() {
        StringBuilder digits = new StringBuilder();

        do {
            digits.append(currentChar);
            if (getch() == -1) {
                break;
            }
        } while (isDigit(currentChar));

        numberText = digits.toString();
        currentSymbol = Symbol.NUMBER;

        if (numberText.length() > MAX_NUMBER_LENGTH) {
            error(30);
        }
        if (isLowercaseLetter(currentChar)) {
            error(39);
            recordErrorToken("", "number cannot be followed by a letter");
        }
    }

    /** 识别 :=。单独出现的 : 仍然是词法错误。 */
    private boolean readBecomes() {
        if (getch() == -1) {
            return false;
        }
        if (currentChar != '=') {
            currentSymbol = Symbol.NUL;
            error(19);
            recordErrorToken(":", "':' must be followed by '='");
            return false;
        }

        currentSymbol = Symbol.BECOMES;
        getch();
        return true;
    }

    /** 识别 < 或 <=。 */
    private boolean readLessOperator() {
        if (getch() == -1) {
            return false;
        }
        if (currentChar == '=') {
            currentSymbol = Symbol.LEQ;
            getch();
        } else {
            currentSymbol = Symbol.LSS;
        }
        return true;
    }

    /** 识别 > 或 >=。 */
    private boolean readGreaterOperator() {
        if (getch() == -1) {
            return false;
        }
        if (currentChar == '=') {
            currentSymbol = Symbol.GEQ;
            getch();
        } else {
            currentSymbol = Symbol.GTR;
        }
        return true;
    }

    /** 识别 +、-、=、; 等单字符特殊符号。 */
    private boolean readSingleSpecialCharacter() {
        Symbol symbol = specialCharacters.get(currentChar);
        if (symbol == null) {
            error(37);
            recordErrorToken(String.valueOf(currentChar), "illegal character");
            getch();
            return false;
        }

        currentSymbol = symbol;
        if (currentSymbol != Symbol.PERIOD) {
            getch();
        }
        return true;
    }

    /** 输出教材格式的词法错误位置。 */
    private void error(int code) {
        int spaceCount = (currentColumn > 0 && currentColumn <= ERROR_LINE_WIDTH)
                ? currentColumn - 1
                : ERROR_LINE_WIDTH;

        StringBuilder message = new StringBuilder("****");
        for (int i = 0; i < spaceCount; i++) {
            message.append(' ');
        }
        message.append('!').append(code);

        System.out.print(message);
        System.out.print('\n');
        errorCount++;
    }

    /** 把 currentSymbol 转成输出表中的一行。 */
    private void recordCurrentToken() {
        if (currentSymbol == Symbol.IDENT) {
            addToken(identifier, TokenCategory.IDENTIFIER, identifier);
        } else if (currentSymbol == Symbol.NUMBER) {
            addToken(numberText, TokenCategory.INTEGER, numberText);
        } else if (KEYWORD_SYMBOLS.contains(currentSymbol)) {
            addToken(identifier, TokenCategory.KEYWORD, currentSymbol.name());
        } else if (SPECIAL_SYMBOLS.contains(currentSymbol)) {
            addToken(symbolText(currentSymbol), TokenCategory.SPECIAL, currentSymbol.name());
        }
    }

    private void recordErrorToken(String word, String message) {
        addToken(word, TokenCategory.ERROR, message);
    }

    private void addToken(String word, TokenCategory category, String value) {
        tokens.add(new Token(word, category, value));
    }

    private String symbolText(Symbol symbol) {
        switch (symbol) {
            case PLUS: return "+";
            case MINUS: return "-";
            case TIMES: return "*";
            case SLASH: return "/";
            case EQL: return "=";
            case NEQ: return "#";
            case LSS: return "<";
            case LEQ: return "<=";
            case GTR: return ">";
            case GEQ: return ">=";
            case LPAREN: return "(";
            case RPAREN: return ")";
            case COMMA: return ",";
            case SEMICOLON: return ";";
            case PERIOD: return ".";
            case BECOMES: return ":=";
            default: return "";
        }
    }

    private boolean isLowercaseLetter(char value) {
        return value >= 'a' && value <= 'z';
    }

    private boolean isDigit(char value) {
        return value >= '0' && value <= '9';
    }

    /** 打印最终 Token 表。 */
    private void printTokens() {
        System.out.print("\nWord                Category            Value\n");
        for (Token token : tokens) {
            System.out.printf("%-20s%-20s%-20s\n",
                    token.word, token.category, token.value);
        }
    }
}
