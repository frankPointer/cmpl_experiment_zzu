import java.io.*;
import java.util.HashMap;

public class LexicalAnalysisUtil {

    public static String filePreprocess(String fileName) {
        String preProcessedString;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 读取源文件
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));


            // 对文件进行预处理
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                line = line.replaceAll("#", "# "); // 匹配#号
                line = line.replaceAll("([()\\[\\]';&])", " $1 "); // 匹配、[]、()、'、"
                line = line.replaceAll("(%[a-zA-Z]|\\\\[a-z])", " $1 "); // 匹配占位符，转义符
                line = line.replaceAll("(\\+\\+|--|=|\\+|-|>>|<<|>=|<=|==|\\+=|-=|/=|:|:=)", " $1 "); // 运算符、界符
                line = line.replaceAll("[\\r\\t\\n]", ""); // 匹配换行、制表
                line = line.replaceAll("\\s+", " "); // 匹配多个空格
                line = line.replaceAll("//.*", ""); // 匹配单行注释

                stringBuilder.append(line).append(" ");
            }
            preProcessedString = stringBuilder.toString();
            // 二次处理
            preProcessedString = preProcessedString.replaceAll("/\\\\*.*?\\\\*/", ""); // 匹配多行注释
            preProcessedString = preProcessedString.replaceAll("\\s+", " "); // 再次清理空格

              //查看预处理之后的内容
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("attachments/demo_preprocessed.txt"));
//            bufferedWriter.write(preProcessedString);
//            bufferedWriter.close();

            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return preProcessedString;
    }

    public static void lexicalAnalysis(String preProcessedString, String outputFilePath) throws IOException {
        HashMap<String, Integer> breedCode = Constants.getBreedCode();
        String[] words = preProcessedString.split(" ");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFilePath));
        String str;
        for (String word : words) {
            if (breedCode.containsKey(word)) {
                str = "( " + breedCode.get(word) + ", " + word + ")";

            } else if (word.matches("^[_a-zA-Z][a-zA-Z0-9_]*$")) { // 匹配ID
                str = "( " + breedCode.get("ID") + ", " + word + ")";

            } else if (word.matches("^[1-9]\\d*$|^\\d&")) { // 匹配NUM
                str = "( " + breedCode.get("NUM") + ", " + word + ")";

            } else if (word.matches("^<[a-z]*\\.h>$")) {  // 用来匹配C语言的头文件
                str = "( " + breedCode.get("库文件") + ", " + word + ")";
            } else if (word.matches("^%[a-zA-Z]$")) {  // 用来匹配占位符
                str = "( " + breedCode.get("占位符") + ", " + word + ")";
            } else if (word.matches("^\\\\[a-z]$")) {  // 用来匹配转义符
                str = "( " + breedCode.get("转义符") + ", " + word + ")";
            } else {
                str = "( error, " + word+")";

            }
            bufferedWriter.write(str);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
