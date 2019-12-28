package io.whileaway.sshd;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;

public class SSHDemo1 {

    public static void main(String[] args) throws IOException {
        String cmd="ifconfig";
        SshClient client=SshClient.setUpDefaultClient();
        client.start();
        ClientSession session=client.connect("bellring", "10.2.48.179", 22).await().getSession();
        session.addPasswordIdentity("bellring");
         //session.addPublicKeyIdentity(SecurityUtils.loadKeyPairIdentity("keyname", new FileInputStream("priKey.pem"), null));
        if(!session.auth().isSuccess())
            System.out.println("auth failed");

        ChannelExec ec=session.createExecChannel(cmd);
        ec.setOut(System.out);
        ec.open();
        ec.waitFor(ClientChannel.CLOSE_WAIT_TIMEOUT, 0);
        ec.close();

        client.stop();
    }
}
