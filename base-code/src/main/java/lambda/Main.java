package lambda;

public class Main {

    public static void main(String[] args) {
        new Runnable() {
            @Override
            public void run() {
                System.out.println("do some thing");
            }
        };

        Runnable run1 = () -> System.out.println("do some thing");

    }


}
