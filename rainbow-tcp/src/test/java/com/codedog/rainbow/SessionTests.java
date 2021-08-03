/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow;

import com.codedog.rainbow.tcp.JsonPacket;
import com.codedog.rainbow.tcp.MessageHandlerException;
import com.codedog.rainbow.tcp.TcpProperties;
import com.codedog.rainbow.tcp.session.DummySession;
import com.codedog.rainbow.tcp.session.Session;
import com.codedog.rainbow.tcp.util.BaseError;
import com.codedog.rainbow.tcp.util.Payload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * SessionTests class
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class SessionTests {

    Session newSession() {
        TcpProperties properties = new TcpProperties();
        return new DummySession(properties) {
            @Override
            protected Object beforeWrite(Object message) {
                if (message == null) return null;
                if (message instanceof BaseError) {
                    message = JsonPacket.errorOf((BaseError) message);
                }
                if (message instanceof MessageHandlerException) {
                    message = JsonPacket.errorOf((MessageHandlerException) message);
                }
                JsonPacket msg = (JsonPacket) message;
                int sn = store.incrSequenceNumberAndGet();
                msg.setSn(sn);
                msg.setAck(store.getAckNumber().get());
                // msg.setSync(((JsonPacket) processingRequest).getSync());
                msg.setTime(System.currentTimeMillis());
                // 缓存起来
                store.cacheResponse(sn, message);
                return msg;
            }
        };
    }

    @Test
    void testSessionWrite() {
        Session session = newSession();

        Object error = new BaseError(1, "some error");
        session.write(error);

        MessageHandlerException ex = new MessageHandlerException(1, "some error");
        session.write(ex);

        JsonPacket packet = JsonPacket.of("Echo", Payload.of("text", "hello"));
        session.write(packet);

    }
}
