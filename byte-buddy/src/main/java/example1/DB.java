package example1;

import com.sun.tools.javac.Main;
import example1.interceptor.Interceptor;
import lombok.NoArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;

import java.lang.reflect.Constructor;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.named;

@NoArgsConstructor
public class DB {
    public String hello(String name) {
        System.out.println("DB:" + name);
        return null;
    }
    public DB(String name) { System.out.println("DB:" + name); }

    public static void main(String[] args) throws Exception {
        interceptorStaticMethod();
        interceptorMethod();
        constructorInterceptor();
    }

    private static void interceptorStaticMethod() throws Exception{
        String helloWorld = new ByteBuddy()
                .subclass(DB.class)
                .method(named("hello"))
                // 拦截DB.hello()方法，并委托给 Interceptor中的静态方法处理
                .intercept(MethodDelegation.to(Interceptor.class))
                .make()
                .load(ClassLoader.getSystemClassLoader(), INJECTION)
                .getLoaded()
                .newInstance()
                .hello("World");
        System.out.println(helloWorld);
    }

    private static void interceptorMethod() throws Exception{
        String hello = new ByteBuddy()
                .subclass(DB.class)
                .method(named("hello"))
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .withBinders(
                                // 要用@Morph注解之前，需要通过 Morph.Binder 告诉 Byte Buddy
                                // 要注入的参数是什么类型
                                Morph.Binder.install(OverrideCallable.class)
                        )
                        .to(new Interceptor()))
                .make()
                .load(Main.class.getClassLoader(), INJECTION)
                .getLoaded()
                .newInstance()
                .hello("World");
        System.out.println(hello);
    }

    private static void constructorInterceptor() throws Exception{
        Constructor<? extends DB> constructor = new ByteBuddy()
                .subclass(DB.class)
                .constructor(any()) // 通过constructor()方法拦截所有构造方法
                // 拦截的操作：首先调用目标对象的构造方法，根据前面自动匹配，
                // 这里直接匹配到参数为String.class的构造方法
                .intercept(SuperMethodCall.INSTANCE.andThen(
                        // 执行完原始构造方法，再开始执行interceptor的代码
                        MethodDelegation.withDefaultConfiguration()
                                .to(new Interceptor())
                )).make().load(Main.class.getClassLoader(), INJECTION)
                .getLoaded()
                .getConstructor(String.class);
        // 下面通过反射创建生成类型的对象
        constructor.newInstance("MySQL");
    }
}
