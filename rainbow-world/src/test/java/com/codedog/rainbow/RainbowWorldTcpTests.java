package com.codedog.rainbow;

import com.codedog.rainbow.tcp.TcpClient;
import com.codedog.rainbow.world.generated.CommonProto.Echo;
import com.google.protobuf.MessageOrBuilder;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

class RainbowWorldTcpTests {

    @Test
    public void test1() throws InterruptedException {
        TcpClient client = new TcpClient(5000);
        MessageOrBuilder request = Echo.newBuilder().setText("hey man, what's up?");
        CompletableFuture<?> future = client.send(request);
        Thread.sleep(100000);
    }
}
