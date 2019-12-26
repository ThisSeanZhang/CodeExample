package io.whileaway.jsch;

import com.jcraft.jsch.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JumpHostsMyVersion {



    public static class MyUserInfo implements UserInfo {

        private String password;

        private MyUserInfo(String password) {
            this.password = password;
        }
        public String getPassphrase() {
            return null;
        }

        public String getPassword() {
            return password;
        }

        public boolean promptPassword(String s) {
            return true;
        }

        public boolean promptPassphrase(String s) {
            System.out.println("promptPassphrase: " + s);
            return true;
        }

        public boolean promptYesNo(String s) {
            // 可以在此进行捕获服务器的RSA 然后进行判断是否通过
            System.out.println("promptYesNo: " + s + "\n end");
            return true;
        }

        public void showMessage(String message) {
            System.out.println("UserInfo message" + message);
        }

    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceInfo {
        private String host;
        private Integer port;
        private String username;
        private String password;

        private Session getSession() throws Exception {
                Session session = new JSch().getSession(username, host, port);
                if (Objects.isNull(session)) throw new Exception("session 创建失败");
                session.setUserInfo(new MyUserInfo(password));
                session.setConfig("userauth.gssapi-with-mic", "no");
                session.connect();
                return session;
        }

        private Session forwardingSession(Session session) throws Exception {
            int assinged_port = session.setPortForwardingL(0, host, port);
            System.out.println("portforwarding: "+ "localhost:"+assinged_port+" -> "+host+":"+22);
            Session session2 = new JSch().getSession(username, "127.0.0.1", assinged_port);
            if (Objects.isNull(session2)) throw new Exception("session 创建失败");
            session2.setUserInfo(new MyUserInfo(password));
            session2.setHostKeyAlias(host);
            session2.connect();
            return session2;
        }
    }

    public static void main(String[] args){
        ServiceInfo service1= new ServiceInfo();
        ServiceInfo service2= new ServiceInfo();
        List<ServiceInfo> services = Arrays.asList(service1, service2);
        List<String> result = connectServerDoSomeThing(services,JumpHostsMyVersion::execCmd)
                .orElseGet(ArrayList::new);
        result.forEach(System.out::println);
    }

    private static List<String> execCmd(Session session) {
        try {
            ChannelExec exec = (ChannelExec)session.openChannel("exec");
            exec.setErrStream(System.err);
            exec.setCommand("ls -la");
            exec.setInputStream(null);
            InputStream in;
            in = exec.getInputStream();
            exec.connect();
            List<String> result = new ArrayList<>();
            while (true) {
                result.addAll(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.toList()));
                if (exec.isClosed()) {
                    break;
                }
                try{Thread.sleep(1000);} catch (Exception ignored) {}
            }
            exec.disconnect();
            return result;
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static<A> Optional<A> connectServerDoSomeThing(List<ServiceInfo> servers, Function<Session, A> concert) {
        if (Objects.isNull(servers) || servers.isEmpty()) {
            return Optional.empty();
        }
        Session[] sessions = new Session[servers.size()];
        Session session;
        try {
            session = sessions[0] = servers.get(0).getSession();
            for (int i = 1 ;i < servers.size() ; i++) {
                session = sessions[i] = servers.get(i).forwardingSession(session);
            }
            Optional<A> result = Optional.ofNullable(concert.apply(session));
            closeAllTable(sessions);
            return result;
        } catch (Exception e) {
            closeAllTable(sessions);
            e.printStackTrace();
        }
        return Optional.empty();
    }
    private static void closeAllTable(Session[] sessions) {
        for(int i = sessions.length-1; i >= 0; i--){
            Session session = sessions[i];
            if (Objects.nonNull(session) && session.isConnected()) {
                session.disconnect();
            }
            if (Objects.nonNull(session)) {
                System.out.println(session.getUserName() + "@" +session.getHost() + ":" + session.getPort() +" is still connection ? :" + session.isConnected());
            }
        }
    }

}
