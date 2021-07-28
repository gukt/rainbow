package com.codedog.rainbow;

import com.codedog.rainbow.tcp.TcpClient;
import com.codedog.rainbow.world.generated.GameEnterRequest;
import org.junit.jupiter.api.Test;

class RainbowWorldTcpTests {

    @Test
    public void test1() throws InterruptedException {
        TcpClient client = new TcpClient(5000);
        GameEnterRequest request = GameEnterRequest.newBuilder().setUid(1).build();
        client.send(request);
        Thread.sleep(10000000);
    }
}
