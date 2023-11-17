import java.util.*;

public class LL1Parser {
    private char START_SYMBOL;
    private Map<Character, List<String>> grammar;
    private Map<Character, Set<Character>> first;
    private Map<Character, Set<Character>> follow;
    private Set<Character> VT;
    private Map<Character, Map<Character, String>> ll1Table;

    public LL1Parser() {
        initGrammar();
    }

    private void initGrammar() {
        getGrammar();
        calculateFirst();
        calculateFollow();
        calculateLL1Table();
    }

    private void getGrammar() {
        this.grammar = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入文法数量以及文法开始符号，然后按照 A->**|** 格式输入文法");

        // 读取文法数量以及开始符号
        String string = sc.nextLine();
        String[] splits = string.split(" ");
        int count = Integer.parseInt(splits[0]);
        START_SYMBOL = splits[1].charAt(0);

        for (int i = 0; i < count; i++) {
            String line = sc.nextLine();
            String[] split = line.substring(3).split("\\|");
            this.grammar.put(line.charAt(0), Arrays.asList(split));
        }
    }

    private void calculateFirst() {
        this.first = new HashMap<>();
        for (char key : this.grammar.keySet()) {
            List<String> production = grammar.get(key);
            this.first.put(key, new HashSet<>());

            for (String value : production) {
                if (!Character.isUpperCase(value.charAt(0))) { // 不是非终结符
                    this.first.get(key).add(value.charAt(0));
                }
            }

        }

        for (int i = 0; i < 2; i++) {
            for (char key : this.grammar.keySet()) {
                List<String> production = grammar.get(key);

                for (String value : production) {
                    if (Character.isUpperCase(value.charAt(0))) { // 不是非终结符
                        this.first.get(key).addAll(this.first.get(value.charAt(0)));
                    }
                }

            }
        }


    }

    private void calculateFollow() {
        this.follow = new HashMap<>();

        // 为文法开始符添加#
        for (Character VN : grammar.keySet()) {
            this.follow.put(VN, new HashSet<>());
            if (VN == START_SYMBOL) {
                this.follow.get(VN).add('#');
            }
        }

        for (int i = 0; i < 3; i++) {
            for (Character VN : this.grammar.keySet()) {

                // 得到产生式
                List<String> productions = grammar.get(VN);
                for (String production : productions) {
                    char lastChar = production.charAt(production.length() - 1);

                    // S->αA，将 S 的 Follow 集加到 A 的 Follow 集中
                    if (Character.isUpperCase(lastChar)) {
                        follow.get(lastChar).addAll(this.follow.get(VN));
                    }


                    for (int j = 0; j < production.length() - 1; j++) {
                        if (Character.isUpperCase(production.charAt(j))) {
                            if (Character.isUpperCase(production.charAt(j + 1))) { // 若A→αBβ是一个产生式，则把FIRST(β)\{ε}加至FOLLOW(B)中
                                follow.get(production.charAt(j)).addAll(first.get(production.charAt(j + 1)));
                                follow.get(production.charAt(j)).remove('$'); // ε
                            } else if (production.charAt(j + 1) != '$') { // A->
                                follow.get(production.charAt(j)).add(production.charAt(j + 1));
                            }

                            boolean emptyFlag = true; // 用来判断是否含有 ε
                            for (int k = j + 1; k < production.length(); k++) {

                                // 后面的字符是终结符 或 是非终结符号但不含有ε
                                if (!Character.isUpperCase(production.charAt(k)) || (Character.isUpperCase(production.charAt(k)) && !first.get(production.charAt(k)).contains('$'))) {
                                    emptyFlag = false;
                                    break;
                                }
                            }
                            // 循环结束之后emptyFlag为真，表示当前字符后面的都是非终结符而且能推导出空串
                            if (emptyFlag) {
                                follow.get(production.charAt(j)).addAll(follow.get(VN));
                                follow.get(production.charAt(j)).remove('$');
                            }
                        }
                    }

                }
            }
        }

    }

    private void calculateVT() {
        VT = new HashSet<>();

        VT.add('#');
        for (List<String> productions : grammar.values()) {
            for (String production : productions) {
                for (char c : production.toCharArray()) {
                    if (!Character.isUpperCase(c) && c != '$') {
                        VT.add(c);
                    }
                }
            }
        }
    }

    private void calculateLL1Table() {
        ll1Table = new HashMap<>();
        calculateVT();

        // 初始化表格为空
        for (Character VN : grammar.keySet()) {
            ll1Table.put(VN, new HashMap<>());
            for (Character vt : VT) {
                ll1Table.get(VN).put(vt, null);
            }
        }

        for (Character VN : grammar.keySet()) {
            List<String> productions = grammar.get(VN);

            for (String production : productions) {
                if (Character.isUpperCase(production.charAt(0))) {
                    for (char vt : VT) {
                        if (first.get(production.charAt(0)).contains(vt)) {
                            ll1Table.get(VN).put(vt, production);
                        }
                    }
                } else {
                    if (production.charAt(0) != '$') {
                        ll1Table.get(VN).put(production.charAt(0), production);
                    } else {
                        for (char f : follow.get(VN)) {
                            ll1Table.get(VN).put(f, production);
                        }
                    }
                }
            }
        }
    }

    public void analyze(String sentence) {
        boolean flag = true;
        ArrayList<String[]> processList = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        stack.push('#');
        stack.push(this.START_SYMBOL);
        processList.add(new String[]{stack.toString(), sentence, ""});

        while (!stack.empty()) {
            char top = stack.peek();
            char input = sentence.charAt(0);
            if (VT.contains(top)) {
                if (top == input) {
                    sentence = sentence.substring(1);
                    stack.pop();
                    processList.add(new String[]{stack.toString(), sentence, ""});
                } else {
                    flag = false;
                    processList.add(new String[]{"error!!!"});
                    break;
                }
            } else {
                if (top == '#') {
                    if (top != input) {
                        flag = false;
                        processList.add(new String[]{"error!!!"});
                    }
                    break;
                } else {
                    String production = ll1Table.get(top).get(input);
                    if (production != null) {
                        stack.pop();
                        if (production.charAt(0) != '$') {
                            for (int i = production.length() - 1; i >= 0; i--) {
                                stack.push(production.charAt(i));
                            }
                        }
                        processList.add(new String[]{stack.toString(), sentence, top + "->" + production});
                    } else {
                        flag = false;
                        processList.add(new String[]{"error!!!", "", ""});
                        break;
                    }
                }
            }
        }
        System.out.println("分析过程：");
        // print processList
        for (int i = 0; i < processList.size(); i++) {
            System.out.printf("%d\t%-20s\t%15s\t%10s\n", i, processList.get(i)[0], processList.get(i)[1], processList.get(i)[2]);
        }

        if (flag) {
            System.out.println("句子符合文法规则");
        } else {
            System.out.println("句子不符合文法规则");
        }
    }


    // 打印LL1文法中的first集、follow集、预测分析表
    public void printLL1Parser() {

        System.out.println("文法为：");
        for (Map.Entry<Character, List<String>> characterListEntry : grammar.entrySet()) {
            List<String> value = characterListEntry.getValue();
            Character key = characterListEntry.getKey();
            String production = String.join("|", value);
            System.out.println(key + "->" + production);
        }

        System.out.println("first集为：");
        for (Map.Entry<Character, Set<Character>> firstEntrySet : first.entrySet()) {
            Character vn = firstEntrySet.getKey();
            Set<Character> firstValue = firstEntrySet.getValue();
            System.out.println(vn + "=" + firstValue);
        }

        System.out.println("follow集为：");
        for (Map.Entry<Character, Set<Character>> followEntrySet : follow.entrySet()) {
            Character vn = followEntrySet.getKey();
            Set<Character> followValue = followEntrySet.getValue();
            System.out.println(vn + "=" + followValue);
        }

        System.out.println("预测分析表为：");
        // 打印表头
        System.out.print(" \t|");
        for (Character vt : ll1Table.get(START_SYMBOL).keySet()) {
            System.out.printf("%-8s |", vt);
        }
        System.out.println();

        // 打印分隔线
        System.out.println("-----------------------------------------------------------------");

        // 打印每行的内容
        for (Character nonTerminal : ll1Table.keySet()) {
            System.out.print(nonTerminal + "\t|");
            Map<Character, String> row = ll1Table.get(nonTerminal);
            for (Character terminal : row.keySet()) {
                String production = nonTerminal + "->" + row.get(terminal);
                System.out.printf("%-8s |", production);
            }
            System.out.println();
        }

    }

}
