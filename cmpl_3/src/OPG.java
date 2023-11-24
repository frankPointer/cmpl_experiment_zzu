import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OPG {
    private char START_SYMBOL;
    private final Map<Character, List<String>> grammar;
    private final Map<Character, Set<Character>> firstVT;
    private final Map<Character, Set<Character>> lastVT;
    private final Map<Character, HashMap<Character, Character>> opgTable;
    private final Set<Character> VT;
    private final ArrayList<String[]> processList;

    public OPG() {
        this.grammar = new HashMap<>();
        this.firstVT = new HashMap<>();
        this.lastVT = new HashMap<>();
        this.opgTable = new HashMap<>();
        this.VT = new HashSet<>();
        this.processList = new ArrayList<>();
        getGrammar();
        calculateVT();
        getFirstVT();
        getLastVT();
        getOPGTable();
    }

    private void calculateVT() {
        VT.add('#');
        for (List<String> productions : grammar.values()) {
            for (String production : productions) {
                for (char c : production.toCharArray()) {
                    if (!grammar.containsKey(c)) {
                        VT.add(c);
                    }
                }
            }
        }
    }

    private void getGrammar() {
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

    private void getFirstVT() {

        // 第一条规则
        for (Character VN : grammar.keySet()) {
            List<String> productions = grammar.get(VN);
            this.firstVT.put(VN, new HashSet<>());

            char c;
            for (String production : productions) {
                c = production.charAt(0);
                if (VT.contains(c)) {
                    this.firstVT.get(VN).add(c);
                } else {
                    if (production.length() > 1) {
                        c = production.charAt(1);
                        if (VT.contains(c)) {
                            this.firstVT.get(VN).add(c);
                        }
                    }
                }
            }
        }

        // 第二条规则
        boolean flag = false;
        while (!flag) {
            flag = true;
            for (Character VN : grammar.keySet()) {
                List<String> productions = grammar.get(VN);
                for (String production : productions) {
                    char firstChar = production.charAt(0);
                    if (!VT.contains(firstChar)) {
                        int length = this.firstVT.get(VN).size();
                        this.firstVT.get(VN).addAll(this.firstVT.get(firstChar));
                        if (length != this.firstVT.get(VN).size()) {
                            flag = false;
                        }
                    }
                }
            }
        }
    }

    private void getLastVT() {
        // 第一条规则
        for (Character VN : grammar.keySet()) {
            List<String> productions = grammar.get(VN);
            this.lastVT.put(VN, new HashSet<>());

            char c;
            for (String production : productions) {
                c = production.charAt(production.length() - 1);
                if (VT.contains(c)) {
                    this.lastVT.get(VN).add(c);
                } else {
                    if (production.length() > 1) {
                        c = production.charAt(production.length() - 2);
                        if (VT.contains(c)) {
                            this.lastVT.get(VN).add(c);
                        }
                    }
                }
            }
        }

        // 第二条规则
        boolean flag = false;
        while (!flag) {
            flag = true;
            for (Character VN : grammar.keySet()) {
                List<String> productions = grammar.get(VN);
                for (String production : productions) {
                    char lastChar = production.charAt(production.length() - 1);
                    if (!VT.contains(lastChar)) {
                        int length = this.lastVT.get(VN).size();
                        this.lastVT.get(VN).addAll(this.lastVT.get(lastChar));
                        if (length != this.lastVT.get(VN).size()) {
                            flag = false;
                        }
                    }
                }
            }
        }
    }

    private void getOPGTable() {
        // 表格置空
        for (Character vt : VT) {
            this.opgTable.put(vt, new HashMap<>());
            for (Character c : VT) {
                opgTable.get(vt).put(c, null);
            }
        }

        // 对于#
        this.opgTable.get('#').put('#', '=');
        for (Character first : this.firstVT.get(this.START_SYMBOL)) {
            this.opgTable.get('#').put(first, '<');
        }
        for (Character last : this.lastVT.get(this.START_SYMBOL)) {
            this.opgTable.get(last).put('#', '>');
        }


        for (Character VN : grammar.keySet()) {
            List<String> productions = grammar.get(VN);
            for (String production : productions) {
                for (int i = 0; i < production.length() - 1; i++) {
                    char pre = production.charAt(i);
                    char order = production.charAt(i + 1);

                    if (VT.contains(pre) && VT.contains(order)) {
                        this.opgTable.get(pre).put(order, '=');
                    }
                    if (i < production.length() - 2) {
                        char post = production.charAt(i + 2);
                        if (VT.contains(pre) && grammar.containsKey(order) && VT.contains(post)) {
                            this.opgTable.get(pre).put(post, '=');
                        }
                    }
                    if (VT.contains(pre) && grammar.containsKey(order)) {
                        for (Character first : this.firstVT.get(order)) {
                            this.opgTable.get(pre).put(first, '<');
                        }
                    }
                    if (VT.contains(order) && grammar.containsKey(pre)) {
                        for (Character last : this.lastVT.get(pre)) {
                            this.opgTable.get(last).put(order, '>');
                        }
                    }
                }
            }
        }
        writeOPGMatrixToFile();
    }

    public void analyze(String sentence) {

        Stack<Character> stack = new Stack<>();
        stack.push('#'); // 将结束符号入栈
        int j;
        Character q;
        Character a;
        sentence = strPreprocess(sentence);

        processList.add(new String[]{stackToString(stack), sentence, "准备"});
        do {
            a = sentence.charAt(0);
            if (VT.contains(stack.peek())) {
                j = stack.size() - 1;
            } else {
                j = stack.size() - 2;
            }
            while (opgTable.get(stack.get(j)).get(a) == '>') {
                do {
                    q = stack.get(j);
                    if (VT.contains(stack.get(j - 1))) {
                        j = j - 1;
                    } else {
                        j = j - 2;
                    }
                } while (opgTable.get(stack.get(j)).get(q) != '<');
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = j + 1; i < stack.size(); i++) {
                    stringBuilder.append(stack.get(i));
                }
                String str = stringBuilder.toString();
                for (Character c : grammar.keySet()) {
                    List<String> productions = grammar.get(c);
                    for (String production : productions) {
                        if (equals(str, production)) {
                            for (int i = 0; i < production.length(); i++) {
                                stack.pop();
                            }
                            stack.push(c);
                            processList.add(new String[]{stackToString(stack), sentence, "规约:" + c + "->" + production});
                        }
                    }
                }
            }
            if (opgTable.get(stack.get(j)).get(a) == '<' || opgTable.get(stack.get(j)).get(a) == '=') {
                stack.push(a);
                sentence = sentence.substring(1);
                processList.add(new String[]{stackToString(stack), sentence, "移进"});
            }
        } while (a != '#');
        writeListToFile();
        System.out.println("分析完成");
        System.out.println("未发生错误");

    }

    private String strPreprocess(String sentence) {
        sentence = sentence.replaceAll("[^()/+\\-*]", "i");
        sentence = sentence.replaceAll("i+", "i");
        return sentence+"#";
    }

    private boolean equals(String stringA, String stringB) {
        if (stringA.length() == stringB.length()) {
            if (stringA.equals(stringB)) {
                return true;
            } else {
                stringA = stringA.replaceAll("[A-Z]", "");
                stringB = stringB.replaceAll("[A-Z]", "");
                return stringA.equals(stringB);
            }
        } else {
            return false;
        }
    }

    private String stackToString(Stack<Character> stack) {
        StringBuilder sb = new StringBuilder();
        for (Character element : stack) {
            sb.append(element);
        }
        return sb.toString();
    }

    private void writeListToFile() {
        String format = "%d\t%-15s\t%18s\t%10s\n";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("attach/" + START_SYMBOL + "_analyze.txt"))) {
            for (int i = 0; i < processList.size(); i++) {
                String[] item = processList.get(i);
                String formatted = String.format(format, i + 1, item[0], item[1], item[2]);
                writer.write(formatted);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOPGMatrixToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("attach/" + START_SYMBOL + "_opg_table.txt"))) {
            // 写入非终结符号的firstVT
            writer.write("FirstVT:\n");
            for (Character vn : grammar.keySet()) {
                writer.write(vn + ": " + firstVT.get(vn) + "\n");
            }
            writer.write("\n");

            // 写入非终结符号的lastVT
            writer.write("LastVT:\n");
            for (Character vn : grammar.keySet()) {
                writer.write(vn + ": " + lastVT.get(vn) + "\n");
            }
            writer.write("\n\n算符优先关系矩阵\n");

            // 写入表头
            writer.write("\t");
            for (Character vt : VT) {
                writer.write(vt + "\t");
            }
            writer.write("\n");

            // 写入每一行的数据
            for (Character vt : VT) {
                writer.write(vt + "\t");
                for (Character c : VT) {
                    Character entry = opgTable.get(vt).get(c);
                    if (entry == null) {
                        writer.write("\t");
                    } else {
                        writer.write(entry + "\t");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
