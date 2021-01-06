package example1;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class Foo {
    public String bar() { return null; }
    public String foo() { return null; }
    public String foo(Object o) { return null; }

    public static void main(String[] args)
            throws NoSuchMethodException, SecurityException ,
            InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Foo foo = new Foo();
        Foo dynamicFoo = new ByteBuddy()
                .subclass(Foo.class)
                .method(isDeclaredBy(Foo.class)) // 匹配 Foo中所有的方法
                .intercept(FixedValue.value("One!"))
                .method(named("foo")) // 匹配名为 foo的方法
                .intercept(FixedValue.value("Two!"))
                .method(named("foo").and(takesArguments(1))) // 匹配名为foo且只有一个
                // 参数的方法
                .intercept(FixedValue.value("Three!"))
                .make()
                .load(Foo.class.getClassLoader(), INJECTION)

                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
        System.out.println(dynamicFoo.bar());
        System.out.println(dynamicFoo.foo());
        System.out.println(dynamicFoo.foo(null));
    }
}
