class Edge {
    //DFA中路径的起点
    private int from;
    //DFA中路径的终点
    private int to;
    //DFA中该路径遇到的字符
    private char path;

    Edge(int from, int to, char path) {
        this.from = from;
        this.to = to;
        this.path = path;
    }

    int getFrom() {
        return from;
    }

    int getTo() {
        return to;
    }

    char getPath() {
        return path;
    }

    @Override
    public String toString() {
        return String.format("I%d--%c->I%d", from, path, to);
    }
}
