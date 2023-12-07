import java.util.*;

public class Parser {

    private static HashMap<String, ArrayList<Integer>> index = new HashMap<>(); // 一个hash，文法产生式为key，编号为value
    private static Grammar[] grammars = new Grammar[1024];
    private static HashSet<Character> terminator = new HashSet<>();
    private static HashSet<Character> non_terminator = new HashSet<>();

    private static void input() {
        String[] in;
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入文法的数目：");
        int n = scanner.nextInt();
        scanner.nextLine();
        System.out.println("请依次输入产生式（示例：A->ab，一行一个）：");
        for (int i = 1; i <= n; i++)
        {
            String line = scanner.nextLine();
            in = line.split("->");
            //按行读入文法
            grammars[i] = new Grammar(line);
            //遍历所有文法
            for (char c : (grammars[i].getFirst() + grammars[i].getSecond()).toCharArray())
            {
                //读取非终结符
                if(Character.isUpperCase(c))
                    non_terminator.add(c);
                else
                    //读取终结符
                    terminator.add(c);
            }
        }
        //在终结符中增加一个“#”
        terminator.add('#');
        String first = grammars[1].getFirst();
        // 扩展文法
        grammars[0] = new Grammar(String.format("%s'->%s", first, first));
        for (int i = 0; i <= n; i++)
        {
            //存储文法的左部，并给文法进行
            if(!index.containsKey(grammars[i].getFirst()))
                index.put(grammars[i].getFirst(), new ArrayList<>());
            // 合并文法左部相同的产生式id
            index.get(grammars[i].getFirst()).add(i);
        }
    }

    // 求闭包
    private static ArrayList<Item> getClosure(Item item) {
        ArrayList<Item> items = new ArrayList<>();
        Queue<Item> queue = new LinkedList<>();
        //将item中所有元素插入队列queue中
        queue.offer(item);
        //广度优先遍历整个队列
        while(!queue.isEmpty()) {
            //移除并返回队列头部元素
            Item now = queue.poll();
            items.add(now);
            int dot = now.getDot();
            //表达式中的点位于表达式的最后时，跳过后来的步骤，执行下一次循环
            if(dot >= now.getSecond().length())
                continue;
            //得到表达式右部中位于点后方的字符
            char c = now.getSecond().charAt(dot);
            // 非终结符，继续搜索
            if(Character.isUpperCase(c)) {
                String first = String.valueOf(c);
                for (int i: index.get(first)) {
                    Item next = new Item(grammars[i].toString(), 0, i);
                    if(next.hasNextDot()) {
                        queue.offer(next);
                    }
                }
            }
        }
        return items;
    }

    public static void main(String[] args) {
        input();
        Item item0 = new Item(grammars[0].toString(), 0, 0);
        ArrayList<ArrayList<Item>> itemsGroup = new ArrayList<>(); // 项目集规范族
        // 将扩展文法的项目集得到并加到项目集族中然后开始计算闭包
        itemsGroup.add(getClosure(item0));

        // 初始化DFA
        ArrayList<Edge> DFA = new ArrayList<>();
        // 遍历项目集族
        for (int i = 0; i < itemsGroup.size(); i++) {
            // 项目集
            ArrayList<Item> items = itemsGroup.get(i);
            // 项目
            for (Item now : items) {
                if (now.hasNextDot()) {
                    // 转换需要的字符
                    char path = now.getSecond().charAt(now.getDot());
                    ArrayList<Item> nextItems = getClosure(now.nextDot());
                    // 得到index看是否在项目集族中
                    int index = itemsGroup.indexOf(nextItems);
                    if (index == -1) // -1表示不在
                    {
                        // index 就是新元素的位置
                        index = itemsGroup.size();
                        itemsGroup.add(nextItems);
                    }
                    // Ii--path-->index
                    DFA.add(new Edge(i, index, path));
                }
            }
        }
        System.out.println(String.format("\n项目集规范族 %d个：", itemsGroup.size()) + itemsGroup);
        System.out.println(String.format("DFA %d条路径：", DFA.size()) + DFA);

        // 构造分析表
        int n = itemsGroup.size();
        //创造两张表，分别表示action和goto
        HashMap<Character, String>[] actionTable = new HashMap[n];
        HashMap<Character, Integer>[] gotoTable = new HashMap[n];

        for(int i = 0; i < n; i++) {
            actionTable[i] = new HashMap<>();
            gotoTable[i] = new HashMap<>();
        }
        //action表中第二行‘#’列的值一直是acc
        actionTable[1].put('#', "acc");

        // DFA中的每一条路径，为非终结符填Goto表，终结符填Action表
        for (Edge edge : DFA) {
            char path = edge.getPath();
            int from = edge.getFrom();
            int to = edge.getTo();
            //判断DFA中该路径遇到的字符是非终结符还是终结符
            if(Character.isUpperCase(path)) {
                //遇到非终结符则填入goto表中
                gotoTable[from].put(path, to);
            }
            else {
                //遇到终结符则填入action表中
                actionTable[from].put(path, String.format("S%d", to));
            }
        }

        //归约
        for (int i = 0; i < n; i++) {
            for (Item item : itemsGroup.get(i)) {
                if(!item.hasNextDot() && i != 1) {
                    for (char c : terminator) {
                        actionTable[i].put(c, String.format("r%d", item.getBelong()));
                    }
                }

            }
        }

        //终结符的数量
        int t_size = terminator.size();
        //非终结符的数量
        int nt_size = non_terminator.size();
        System.out.println("\nLR(0)分析表：");
        System.out.print("\taction\t");
        for (int i = 1; i < t_size; i++)
            System.out.print("\t\t");
        System.out.print("goto");
        for (int i = 0; i < nt_size; i++)
            System.out.print("\t");
        System.out.print("\n\t");
        for (char c : terminator)
            System.out.print(String.format("%-4c\t", c));
        for (char c : non_terminator)
            System.out.print(String.format("%-4c\t", c));
        for (int i = 0; i < n; i++) {
            System.out.print(String.format("\n%d\t", i));
            for (char c : terminator)
                System.out.print(String.format("%-4s\t", actionTable[i].get(c)));
            for (char c : non_terminator)
                System.out.print(String.format("%-4s\t", gotoTable[i].get(c)));
        }

        //  分析输入串
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n请输入要分析的输入串：");
        String str = scanner.nextLine()+"#";
        //状态栈
        Stack<Integer> statusStack = new Stack<>();
        statusStack.push(0);
        //符号栈
        Stack<Character> charStack = new Stack<>();
        charStack.push('#');
        //得到状态栈的栈顶元素
        int status = statusStack.peek();
        //输入串的索引值
        int pos = 0;
        //步骤
        int cnt = 1;
        System.out.println("步骤\t\t状态栈\t\t\t\t符号栈\t\t\t\t\t输入串\t\taction\t\tgoto");
        String action = actionTable[status].get(str.charAt(pos));
        while (!action.equals("acc")) {
            System.out.print(String.format("%d\t\t%-16s\t%-16s\t%8s\t%8s\t\t", cnt++, statusStack, charStack, str.substring(pos), action));
            char a = action.charAt(0);
            //移进
            if(a == 'S') {
                status = Integer.parseInt(action.substring(1));
                statusStack.push(status);
                charStack.push(str.charAt(pos++));
            }
            else {
                int index = Integer.parseInt(action.substring(1));
                Grammar grammar = grammars[index];
                int len = grammar.getSecond().length();
                while(len-- > 0) {
                    statusStack.pop();
                    charStack.pop();
                }
                char c = grammar.getFirst().charAt(0);
                charStack.push(c);
                status = statusStack.peek();
                status = gotoTable[status].get(c);
                statusStack.push(status);
                System.out.print(status);
            }
            action = actionTable[status].get(str.charAt(pos));
            System.out.println();
            if (action == null) {
                System.out.print(String.format("%d\t\t%-16s\t%-16s\t%8s\t%8s\t\t", cnt, statusStack, charStack, str.substring(pos), "err"));
                return;
            }
        }
        System.out.print(String.format("%d\t\t%-16s\t%-16s\t%8s\t%8s\t\t", cnt, statusStack, charStack, str.substring(pos), action));
    }
}
