package example1.interceptor;

import example1.DB;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class Interceptor {

    // 拦截的是静态的方法  并进行改造
    public static String intercept(String name) { return "String"; }
    public static String intercept(int i) { return "int"; }
    public static String intercept(Object o) { return "Object";}

    // 拦截的是动态的方法  并进行改造
    @RuntimeType
    public Object intercept(
            @This Object obj, // 目标对象
            @AllArguments Object[] allArguments, // 注入目标方法的全部参数
            @SuperCall Callable<?> zuper, // 调用目标方法，必不可少哦
            @Origin Method method, // 目标方法
            // 目标对象
            @Super DB db  ) throws Exception {
        System.out.println(obj);
        System.out.println(db);
        // 从上面两行输出可以看出，obj和db是一个对象
        try {
            return zuper.call(); // 调用目标方法
        } finally {
        }
    }

    /**
     * 拦截构造器
     * @param obj
     * @param allArguments
     */
    @RuntimeType
    public void intercept(@This Object obj,
                          @AllArguments Object[] allArguments) {
        System.out.println("after constructor!");
    }
}
