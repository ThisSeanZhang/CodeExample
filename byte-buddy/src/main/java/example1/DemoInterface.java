package example1;

import com.sun.tools.javac.Main;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface DemoInterface {
    String get();
    void set(String name);

    static void main(String[] args) throws Exception{
        Class<? extends Foo> loaded = new ByteBuddy()
                .subclass(Foo.class)
                // 定义方法的名称 返回类型 修饰
                .defineMethod("moon",  String.class, Modifier.PUBLIC)
                .withParameter(String.class, "s") // 新增方法的参数参数
                .intercept(FixedValue.value("Zero!")) // 方法的具体实现，返回固定值
                // 新增一个字段，该字段名称成为"name"，类型是 String，且public修饰
                .defineField("name", String.class, Modifier.PUBLIC)
                .implement(DemoInterface.class) // 实现DemoInterface接口
                // 实现 DemoInterface接口的方式是读写name字段
                .intercept(FieldAccessor.ofField("name"))
                .make()
                .load(Main.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded(); // 获取加载后的Class

        Foo dynamicFoo = loaded.newInstance(); // 反射
        // 要调用新定义的doo()方法，只能通过反射方式
        Method m = loaded.getDeclaredMethod("moon", String.class);
        System.out.println(m.invoke(dynamicFoo, new Object[]{""}));
        Field field = loaded.getField("name"); // 通过反射方式读写新增的name字段
        field.set(dynamicFoo, "Zero-Name");
        System.out.println(field.get(dynamicFoo));

        // 通过反射调用 DemoInterface接口中定义的get()和set()方法，读取name字段的值
        Method setNameMethod = loaded.getDeclaredMethod("set", String.class);
        setNameMethod.invoke(dynamicFoo, new Object[]{"Zero-Name2"});
        Method getNameMethod = loaded.getDeclaredMethod("get");
        System.out.println(getNameMethod.invoke(dynamicFoo, new Object[]{}));
    }
}
