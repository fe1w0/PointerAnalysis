package hello;

class A {

}

public class Hello {
    public static void main(String[] args) {
        A a = new A();
        A b = new A();
        A c = new A();
        if (args.length > 2) a = b;
    }
}
