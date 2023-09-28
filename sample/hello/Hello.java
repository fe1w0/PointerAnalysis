package hello;

class A {

    public void test(){
    }
}

public class Hello {
    public static void main(String[] args) {
        A a = new A();
        A b = new A();
        A c = new A();
        if (args.length > 2) {
            a = b;
        }
        // 确实是优化问题，不知道是javac生成的class的问题，还是soot分析的问题（我觉得前者的可能性大一点）。
        a.test();
    }
}
