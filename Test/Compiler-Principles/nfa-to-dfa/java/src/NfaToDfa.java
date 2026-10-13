import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/** NFA 转 DFA：使用栈求 ε-闭包，使用队列执行子集构造。 */
public class NfaToDfa {
    private static final char EPSILON = '$';

    static final class Nfa {
        final int stateCount;
        final int startState;
        final Set<Integer> acceptingStates;
        final Set<Character> alphabet;
        final List<Map<Character, Set<Integer>>> transitions = new ArrayList<>();
        final List<Set<Integer>> epsilonTransitions = new ArrayList<>();

        Nfa(int stateCount, int startState, Set<Integer> acceptingStates,
                Set<Character> alphabet) {
            this.stateCount = stateCount;
            this.startState = startState;
            this.acceptingStates = acceptingStates;
            this.alphabet = alphabet;

            // 每个状态都要有独立的普通转换表和 ε 目标集合。
            for (int state = 0; state < stateCount; state++) {
                transitions.add(new HashMap<>());
                epsilonTransitions.add(new HashSet<>());
            }
        }

        void addEdge(int fromState, int toState, char symbol) {
            if (symbol == EPSILON) {
                epsilonTransitions.get(fromState).add(toState);
            } else {
                Map<Character, Set<Integer>> stateTransitions = transitions.get(fromState);
                Set<Integer> targets = stateTransitions.get(symbol);
                if (targets == null) {
                    targets = new HashSet<>();
                    stateTransitions.put(symbol, targets);
                }
                targets.add(toState);
            }
        }
    }

    static final class Dfa {
        final int startState = 0;
        final Set<Character> alphabet;
        final List<Set<Integer>> states = new ArrayList<>();
        final List<Map<Character, Integer>> transitions = new ArrayList<>();
        final Set<Integer> acceptingStates = new HashSet<>();

        Dfa(Set<Character> alphabet) {
            this.alphabet = new LinkedHashSet<>(alphabet);
        }
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            System.err.println("Usage: java NfaToDfa < input.txt");
            System.exit(1);
            return;
        }

        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));
            Nfa nfa = readNfa(input);
            Dfa dfa = buildDfa(nfa);
            printDfa(dfa);
        } catch (IllegalArgumentException exception) {
            System.err.println("Input error: " + exception.getMessage());
            System.exit(1);
        } catch (IOException exception) {
            System.err.println("Read failed: " + exception.getMessage());
            System.exit(1);
        }
    }

    /** 按固定行格式读取；读够声明的边数即返回，不等待额外的 EOF。 */
    static Nfa readNfa(BufferedReader input) throws IOException {
        String header = readRequiredLine(input, "line 1 (states)");
        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }
        String[] parts = splitFields(header);
        if (parts.length < 3) {
            throw new IllegalArgumentException("line 1 needs state count, start and final count");
        }

        int stateCount = parseInteger(parts[0], "state count");
        int startState = parseInteger(parts[1], "start state");
        int acceptingCount = parseInteger(parts[2], "final count");
        if (stateCount <= 0) {
            throw new IllegalArgumentException("state count must be positive");
        }
        validateState(startState, stateCount, "start state");
        if (acceptingCount < 0 || acceptingCount > stateCount) {
            throw new IllegalArgumentException("final count must be between 0 and state count");
        }
        if (parts.length - 3 != acceptingCount) {
            throw new IllegalArgumentException("final count does not match final IDs");
        }

        Set<Integer> acceptingStates = new HashSet<>();
        for (int i = 0; i < acceptingCount; i++) {
            int state = parseInteger(parts[3 + i], "final state");
            validateState(state, stateCount, "final state");
            if (!acceptingStates.add(state)) {
                throw new IllegalArgumentException("duplicate final state: " + state);
            }
        }

        Set<Character> alphabet = new LinkedHashSet<>();
        String[] symbolParts = splitFields(readRequiredLine(input, "line 2 (alphabet)"));
        for (String symbolText : symbolParts) {
            char symbol = parseSymbol(symbolText);
            if (symbol == EPSILON) {
                throw new IllegalArgumentException("alphabet must not contain $");
            }
            alphabet.add(symbol);
        }

        String[] countParts = splitFields(readRequiredLine(input, "line 3 (edge count)"));
        if (countParts.length != 1) {
            throw new IllegalArgumentException("line 3 must contain one edge count");
        }
        int edgeCount = parseInteger(countParts[0], "edge count");
        if (edgeCount < 0) {
            throw new IllegalArgumentException("edge count cannot be negative");
        }

        Nfa nfa = new Nfa(stateCount, startState, acceptingStates, alphabet);
        for (int i = 0; i < edgeCount; i++) {
            String location = "line " + (i + 4) + " (edge " + (i + 1) + ")";
            String[] edgeParts = splitFields(readRequiredLine(input, location));
            if (edgeParts.length != 3) {
                throw new IllegalArgumentException(location + " needs: from to symbol");
            }
            int fromState = parseInteger(edgeParts[0], location + " source");
            int toState = parseInteger(edgeParts[1], location + " target");
            validateState(fromState, stateCount, location + " source");
            validateState(toState, stateCount, location + " target");
            char symbol = parseSymbol(edgeParts[2]);
            if (symbol != EPSILON && !alphabet.contains(symbol)) {
                throw new IllegalArgumentException(location + " undeclared symbol: " + symbol);
            }
            nfa.addEdge(fromState, toState, symbol);
        }
        return nfa;
    }

    /** 结果集合记录已发现状态，栈记录尚待检查 ε 边的状态。 */
    static Set<Integer> epsilonClosure(Nfa nfa, Set<Integer> seedStates) {
        Set<Integer> closure = new HashSet<>(seedStates);
        Deque<Integer> stack = new ArrayDeque<>();
        for (int state : seedStates) {
            stack.push(state);
        }

        while (!stack.isEmpty()) {
            int currentState = stack.pop();
            for (int targetState : nfa.epsilonTransitions.get(currentState)) {
                if (!closure.contains(targetState)) {
                    // 压栈前就标记为已发现，避免环和重复边导致重复处理。
                    closure.add(targetState);
                    stack.push(targetState);
                }
            }
        }
        return closure;
    }

    /** 仅经过一条指定字符边；不会沿目标继续搜索，也不会处理 ε 边。 */
    static Set<Integer> move(Nfa nfa, Set<Integer> sourceStates, char symbol) {
        Set<Integer> result = new HashSet<>();
        for (int state : sourceStates) {
            Set<Integer> targets = nfa.transitions.get(state).get(symbol);
            if (targets != null) {
                result.addAll(targets);
            }
        }
        return result;
    }

    static Dfa buildDfa(Nfa nfa) {
        Dfa dfa = new Dfa(nfa.alphabet);
        Map<Set<Integer>, Integer> stateIds = new HashMap<>();
        Deque<Integer> pendingStates = new ArrayDeque<>();

        Set<Integer> startSeeds = new HashSet<>();
        startSeeds.add(nfa.startState);
        Set<Integer> startStates = epsilonClosure(nfa, startSeeds);
        addDfaState(dfa, nfa, startStates, stateIds, pendingStates);

        while (!pendingStates.isEmpty()) {
            int currentId = pendingStates.removeFirst();
            Set<Integer> currentStates = dfa.states.get(currentId);

            for (char symbol : dfa.alphabet) {
                Set<Integer> movedStates = move(nfa, currentStates, symbol);
                Set<Integer> nextStates = epsilonClosure(nfa, movedStates);
                Integer targetId = stateIds.get(nextStates);

                if (targetId == null) {
                    // 空集也参与查重：需要时创建一次陷阱状态。
                    targetId = addDfaState(dfa, nfa, nextStates, stateIds, pendingStates);
                }
                // 目标是旧状态时，也必须记录当前这条转换。
                dfa.transitions.get(currentId).put(symbol, targetId);
            }
        }
        return dfa;
    }

    /** 初态和后续新状态都通过这里登记，保证几个容器同步更新。 */
    private static int addDfaState(Dfa dfa, Nfa nfa, Set<Integer> subset,
            Map<Set<Integer>, Integer> stateIds, Deque<Integer> pendingStates) {
        int newStateId = dfa.states.size();
        // Map 的集合键不能再修改；复制并设为只读，排序仅用于稳定展示。
        Set<Integer> savedStates = Collections.unmodifiableSet(new TreeSet<>(subset));
        dfa.states.add(savedStates);
        dfa.transitions.add(new HashMap<>());
        stateIds.put(savedStates, newStateId);
        pendingStates.addLast(newStateId);

        if (containsAcceptingState(savedStates, nfa.acceptingStates)) {
            dfa.acceptingStates.add(newStateId);
        }
        return newStateId;
    }

    static boolean containsAcceptingState(Set<Integer> subset, Set<Integer> acceptingStates) {
        for (int state : subset) {
            if (acceptingStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    static void printDfa(Dfa dfa) {
        System.out.println("DFA state count: " + dfa.states.size());
        System.out.println("Alphabet: " + dfa.alphabet);
        System.out.println("Start state: D" + dfa.startState);
        StringJoiner finalNames = new StringJoiner(", ", "{", "}");
        for (int id : new TreeSet<>(dfa.acceptingStates)) {
            finalNames.add("D" + id);
        }
        System.out.println("Accepting states: " + finalNames);
        System.out.println("\nStates (* = start, + = accepting):");

        for (int id = 0; id < dfa.states.size(); id++) {
            String marks = "";
            if (id == dfa.startState) {
                marks += "*";
            }
            if (dfa.acceptingStates.contains(id)) {
                marks += "+";
            }
            System.out.println(marks + "D" + id + " = " + formatStateSet(dfa.states.get(id)));
        }

        System.out.println("\nTransition table:");
        System.out.print("State");
        for (char symbol : dfa.alphabet) {
            System.out.print("\t" + symbol);
        }
        System.out.println();
        for (int id = 0; id < dfa.states.size(); id++) {
            System.out.print("D" + id);
            for (char symbol : dfa.alphabet) {
                System.out.print("\tD" + dfa.transitions.get(id).get(symbol));
            }
            System.out.println();
        }
        System.out.println("\n{} is the trap state, if reachable. No DFA minimization is performed.");
    }

    private static String formatStateSet(Set<Integer> states) {
        StringJoiner text = new StringJoiner(", ", "{", "}");
        for (int state : new TreeSet<>(states)) {
            text.add(Integer.toString(state));
        }
        return text.toString();
    }

    private static String readRequiredLine(BufferedReader input, String location) throws IOException {
        String line = input.readLine();
        if (line == null) {
            throw new IllegalArgumentException("missing " + location);
        }
        return line;
    }

    private static String[] splitFields(String line) {
        String text = line.trim();
        // 空白的第二行代表空终结符集合；其他行由各自的格式检查拒绝空白。
        return text.isEmpty() ? new String[0] : text.split("\\s+");
    }

    private static int parseInteger(String text, String location) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(location + " must be a 32-bit integer: " + text);
        }
    }

    private static char parseSymbol(String text) {
        if (text.length() != 1 || Character.isSurrogate(text.charAt(0))) {
            throw new IllegalArgumentException("symbol must be one Java char: " + text);
        }
        return text.charAt(0);
    }

    private static void validateState(int state, int stateCount, String location) {
        if (state < 0 || state >= stateCount) {
            throw new IllegalArgumentException(location + " must be in 0.." + (stateCount - 1));
        }
    }
}
