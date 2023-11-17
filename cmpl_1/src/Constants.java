import java.util.HashMap;

public class Constants {
    public static HashMap<String, Integer> getBreedCode() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("#", 0);
        map.put("main", 1);
        map.put("if", 2);
        map.put("then", 3);
        map.put("while", 4);
        map.put("do", 5);
        map.put("static", 6);
        map.put("int", 7);
        map.put("double", 8);
        map.put("struct", 9);
        map.put("break", 10);
        map.put("else", 11);
        map.put("long", 12);
        map.put("switch", 13);
        map.put("case", 14);
        map.put("typedef", 15);
        map.put("char", 16);
        map.put("return", 17);
        map.put("const", 18);
        map.put("float", 19);
        map.put("short", 20);
        map.put("continue", 21);
        map.put("for", 22);
        map.put("void", 23);
        map.put("sizeof", 24);
        map.put("ID", 25);
        map.put("NUM", 26);
        map.put("+", 27);
        map.put("-", 28);
        map.put("*", 29);
        map.put("/", 30);
        map.put(":", 31);
        map.put(":=", 32);
        map.put("<", 33);
        map.put("<>", 34);
        map.put("<=", 35);
        map.put(">", 36);
        map.put(">=", 37);
        map.put("=", 38);
        map.put("default", 39);
        map.put("(", 42);
        map.put(")", 43);

        // 补充的
        map.put("&", 44);
        map.put("++", 45);
        map.put("--", 47);
        map.put("+=", 48);
        map.put("-=", 49);
        map.put("*=", 50);
        map.put("/=", 51);
        map.put(">>", 52);
        map.put("<<", 53);
        map.put("{", 54);
        map.put("}", 55);
        map.put("[", 56);
        map.put("]", 57);
        map.put(";", 58);
        map.put("库文件",59);
        map.put("占位符",60);
        map.put("转义符",61);
        return map;
    }
}
