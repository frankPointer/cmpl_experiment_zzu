public class Grammar {
    //表达式的左部
    private String first;
    //表达式的右部
    private String second;

    /**
     *
     * @return 产生式的左部分
     */
    String getFirst() {
        return first;
    }

    /**
     *
     * @return 产生式的右部分
     */
    String getSecond() {
        return second;
    }

    Grammar(String line) {
        this.first = line.split("->")[0];
        if(line.length() == 3)
            this.second = "";
        else
            this.second = line.split("->")[1];
    }

    @Override
    public String toString() {
        return this.first + "->" + this.second;
    }
}
