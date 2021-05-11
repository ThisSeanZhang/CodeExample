package io.whileaway.code.demo.netty.for_whitout_netty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 未使用 Netty 的同步网络编程
 */
public class PlainOioServer {

    public static void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);
        try {
            while (true) {
                final Socket clientSocket = socket.accept();
                System.out.println(
                        "Accepted connection from " + clientSocket);
                new Thread(() -> {
                    OutputStream out;
                    try (clientSocket) {
                        out = clientSocket.getOutputStream();
                        out.write("Hi!\r\n".getBytes(StandardCharsets.UTF_8));
                        out.flush();
//                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // ignore on close
                }).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
