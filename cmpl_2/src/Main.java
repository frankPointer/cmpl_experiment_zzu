import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LL1Parser ll1Parser = new LL1Parser();
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要分析的句子：");
        String sentence = scanner.nextLine();
        ll1Parser.analyze(sentence);
        ll1Parser.printLL1Parser();
    }
}
