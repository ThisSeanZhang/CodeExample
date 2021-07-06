package clazz;

public class Main {
    public static void main(String[] args) {
        TestInnerClass testInnerClass = new TestInnerClass();
        TestInnerClass.InnerA innerA = testInnerClass.new InnerA();
    }
}
