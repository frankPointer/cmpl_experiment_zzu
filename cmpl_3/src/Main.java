import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        OPG opGrammar = new OPG();
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要分析的句子：");
        String sentence = scanner.nextLine();
        opGrammar.analyze(sentence);

    }

}
