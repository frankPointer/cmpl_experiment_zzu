import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String preprocessedString = LexicalAnalysisUtil.filePreprocess("attachments/demo.txt");
        LexicalAnalysisUtil.lexicalAnalysis(preprocessedString,"attachments/result.txt");
    }



}
