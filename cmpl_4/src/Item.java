public class Item extends Grammar implements Cloneable {
    //LR（0）分析中的点
    private int dot;
    //LR（0）分析中归约的标志
    private int belong;

    Item(String line, int dot, int belong) {
        super(line);
        this.dot = dot;
        this.belong = belong; // 所属grammar
    }

    boolean hasNextDot() {
        return dot < getSecond().length();
    }

    //将LR（0）分析中的点向后移动
    Item nextDot() {
        Item next = null;
        try {
            //克隆该表达式
            next = (Item) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //断言，判断next是否为空，若为空，则抛出异常
        assert next != null;
        //该条表达式的点向后移动
        next.dot++;
        return next;
    }

    @Override
    public boolean equals(Object object) {
        //判断object是否是Item或其子类创建的对象
        if(object instanceof Item)
            //返回带点的表达式
            return this.toString().equals(object.toString());
        return false;
    }

    int getDot() {
        return dot;
    }

    @Override
    public String toString() {
        String second = super.getSecond();
        StringBuilder stringBuilder = new StringBuilder(second);
        stringBuilder.insert(dot, ".");
        second = stringBuilder.toString();
        return super.getFirst() + "->" + second;
    }

    int getBelong() {
        return belong;
    }

}
