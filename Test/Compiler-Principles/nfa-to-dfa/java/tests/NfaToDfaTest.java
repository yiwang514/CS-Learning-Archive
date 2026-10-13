import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/** 独立测试程序，无需 JUnit；不是实验提交必需的源文件。 */
public class NfaToDfaTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        testAlgorithms();
        testInputAndBoundaries();
        testLongChain();
        testLanguageEquivalence();
        System.out.println("[OK] " + checks + " checks passed.");
    }

    private static void testAlgorithms() throws IOException {
        NfaToDfa.Nfa nfa = read("5 0 1 4\na b\n6\n0 1 $\n1 0 $\n0 2 a\n1 3 a\n3 4 a\n2 4 $\n");
        Set<Integer> seed = states(0);
        check(NfaToDfa.epsilonClosure(nfa, seed).equals(states(0, 1)), "epsilon cycle");
        check(seed.equals(states(0)), "seed unchanged");
        check(NfaToDfa.epsilonClosure(nfa, states(2)).equals(states(2, 4)), "epsilon chain");
        check(NfaToDfa.epsilonClosure(nfa, states()).isEmpty(), "empty closure");
        check(NfaToDfa.move(nfa, states(0, 1), 'a').equals(states(2, 3)), "move only one step");
        check(NfaToDfa.move(nfa, states(0), '$').isEmpty(), "move excludes epsilon");

        NfaToDfa.Dfa dfa = NfaToDfa.buildDfa(read("3 0 1 2\na\n2\n0 1 $\n1 2 a\n"));
        check(dfa.states.equals(Arrays.asList(states(0, 1), states(2), states())), "hand-calculated subsets");
        check(dfa.acceptingStates.equals(states(1)), "D1 final");
        check(dfa.transitions.get(0).get('a') == 1, "D0-a-D1");
        check(dfa.transitions.get(1).get('a') == 2, "D1-a-trap");
        check(dfa.transitions.get(2).get('a') == 2, "trap loop");
        try {
            dfa.states.get(0).add(99);
            throw new AssertionError("stored subset is mutable");
        } catch (UnsupportedOperationException expected) {
            checks++;
        }
        NfaToDfa.Dfa merged = NfaToDfa.buildDfa(read("5 0 1 3\na b\n7\n0 1 $\n1 0 $\n0 2 a\n1 2 a\n2 3 $\n3 3 b\n4 4 a\n"));
        check(merged.states.equals(Arrays.asList(states(0, 1), states(2, 3), states(), states(3))), "merge, unreachable state and BFS order");
        check(merged.acceptingStates.equals(states(1, 3)), "mixed subset final");
        check(merged.transitions.get(3).get('b') == 3, "reuse existing state");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try (PrintStream output = new PrintStream(bytes, true, "UTF-8")) {
            System.setOut(output);
            NfaToDfa.printDfa(dfa);
        } finally {
            System.setOut(original);
        }
        String text = new String(bytes.toByteArray(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        check(text.contains("*D0 = {0, 1}") && text.contains("+D1 = {2}"), "state labels");
        check(text.contains("State\ta\nD0\tD1\nD1\tD2\nD2\tD2"), "table");
    }

    private static void testInputAndBoundaries() throws IOException {
        NfaToDfa.Dfa startFinal = NfaToDfa.buildDfa(read("2 1 1 0\na\n1\n1 0 $\n"));
        check(startFinal.states.get(0).equals(states(0, 1)), "nonzero NFA start");
        check(startFinal.acceptingStates.contains(0), "D0 final");
        NfaToDfa.Dfa noFinal = NfaToDfa.buildDfa(read("1 0 0\na b\n0\n"));
        check(noFinal.acceptingStates.isEmpty() && noFinal.states.size() == 2, "no finals and shared trap");
        NfaToDfa.Dfa emptyAlphabet = NfaToDfa.buildDfa(read("1 0 1 0\n\n0\n"));
        check(emptyAlphabet.states.size() == 1 && emptyAlphabet.transitions.get(0).isEmpty(), "empty alphabet");
        NfaToDfa.Nfa duplicate = read("2 0 1 1\na a\n2\n0 1 a\n0 1 a\n");
        check(duplicate.alphabet.size() == 1 && duplicate.transitions.get(0).get('a').equals(states(1)), "duplicates merged");
        check(read("\uFEFF 2 0 1 1\n b\t a \n1\n0 1 a").alphabet.iterator().next() == 'b', "BOM, whitespace, alphabet order");
        String[] invalid = {
            "", "2 0\na\n0\n", "x 0 0\na\n0\n", "0 0 0\na\n0\n",
            "2 2 0\na\n0\n", "2 -1 0\na\n0\n", "2 0 -1\na\n0\n",
            "2 0 2 1\na\n0\n", "2 0 1 2\na\n0\n", "2 0 2 1 1\na\n0\n",
            "2 0 0\n$\n0\n", "2 0 0\nab\n0\n", "2 0 0\na\n-1\n",
            "2 0 0\na\n1 2\n", "2 0 0\na\n1\n", "2 0 0\na\n1\n0 1\n",
            "2 0 0\na\n1\n0 1 b\n", "2 0 0\na\n1\n0 2 a\n",
            "2 0 0\na\n1\n-1 1 a\n", "2 0 0\na\n1\n0 1 aa\n",
            "2147483648 0 0\na\n0\n"
        };
        for (String input : invalid) {
            try {
                read(input);
                throw new AssertionError("invalid input accepted: " + input);
            } catch (IllegalArgumentException expected) {
                checks++;
            }
        }
    }

    private static void testLongChain() {
        int count = 20000;
        NfaToDfa.Nfa nfa = new NfaToDfa.Nfa(count, 0, states(count - 1), Collections.emptySet());
        for (int state = 0; state < count - 1; state++) {
            nfa.addEdge(state, state + 1, '$');
        }
        check(NfaToDfa.epsilonClosure(nfa, states(0)).size() == count, "20000-state iterative closure");
    }

    private static void testLanguageEquivalence() {
        Random random = new Random(20261013L);
        Set<Character> alphabet = new LinkedHashSet<>(Arrays.asList('a', 'b'));
        for (int sample = 0; sample < 40; sample++) {
            int count = 1 + random.nextInt(6);
            NfaToDfa.Nfa nfa = new NfaToDfa.Nfa(count, random.nextInt(count), states(random.nextInt(count)), alphabet);
            for (int from = 0; from < count; from++) {
                for (int to = 0; to < count; to++) {
                    for (char symbol : new char[] {'a', 'b', '$'}) {
                        if (random.nextInt(5) == 0) {
                            nfa.addEdge(from, to, symbol);
                        }
                    }
                }
            }
            NfaToDfa.Dfa dfa = NfaToDfa.buildDfa(nfa);
            check(new HashSet<>(dfa.states).size() == dfa.states.size(), "unique subsets");
            for (int length = 0; length <= 4; length++) {
                for (int mask = 0; mask < (1 << length); mask++) {
                    StringBuilder word = new StringBuilder();
                    for (int bit = 0; bit < length; bit++) {
                        word.append((mask & (1 << bit)) == 0 ? 'a' : 'b');
                    }
                    Set<Integer> current = referenceClosure(nfa, states(nfa.startState));
                    int dfaId = dfa.startState;
                    for (char symbol : word.toString().toCharArray()) {
                        Set<Integer> next = new HashSet<>();
                        for (int state : current) {
                            Set<Integer> targets = nfa.transitions.get(state).get(symbol);
                            if (targets != null) {
                                next.addAll(targets);
                            }
                        }
                        current = referenceClosure(nfa, next);
                        dfaId = dfa.transitions.get(dfaId).get(symbol);
                    }
                    boolean nfaAccepts = !Collections.disjoint(current, nfa.acceptingStates);
                    check(nfaAccepts == dfa.acceptingStates.contains(dfaId), "language equivalence");
                }
            }
        }
    }

    // 使用不动点迭代独立核对，不调用生产代码的栈闭包。
    private static Set<Integer> referenceClosure(NfaToDfa.Nfa nfa, Set<Integer> seeds) {
        Set<Integer> result = new HashSet<>(seeds);
        boolean changed;
        do {
            changed = false;
            for (int state : new HashSet<>(result)) {
                changed |= result.addAll(nfa.epsilonTransitions.get(state));
            }
        } while (changed);
        return result;
    }

    private static NfaToDfa.Nfa read(String text) throws IOException {
        return NfaToDfa.readNfa(new BufferedReader(new StringReader(text)));
    }

    private static Set<Integer> states(Integer... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        checks++;
    }
}
