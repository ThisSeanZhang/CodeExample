package io.whileaway.code;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

public class ConnectionDriver {


    public static Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("commit")) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        return null;
    }

    public static Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(), new Class[] {Connection.class}, ConnectionDriver::invoke);
    }
}
