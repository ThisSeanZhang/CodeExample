package function;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Function2 f = c -> System.out.println("a");
        Map<String, String> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.computeIfAbsent()
        double[] doubles = IntStream.range(0, 10).peek(System.out::println).asDoubleStream().toArray();
//        Arrays.parallelSetAll(doubles, a -> a);
        Arrays.parallelPrefix(doubles, Double::sum);
        Arrays.stream(doubles).forEach(System.out::println);
//        System.out.println(doubles);
    }

}
