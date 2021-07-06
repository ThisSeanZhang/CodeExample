package clazz;

public class TestInnerClass {

    private void test() {
    }
    public class InnerA {
        public void useTest() {
            TestInnerClass.this.test();
        }

    }
}
