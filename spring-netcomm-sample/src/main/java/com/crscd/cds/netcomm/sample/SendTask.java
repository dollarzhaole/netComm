package com.crscd.cds.netcomm.sample;

import com.crscd.cds.spring.netcomm.config.NetCommTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhaole
 * @date 2022-04-29
 */
@Component
public class SendTask {
    private final NetCommTemplate netCommTemplate;

    public SendTask(NetCommTemplate netCommTemplate) {
        this.netCommTemplate = netCommTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void sendData() throws IllegalAccessException {
        TestMessage message = new TestMessage();
        message.setType((short) 0x01);
        message.setFunc((short) 0x01);
        message.setValue1(0x01);
        message.setValue2(0x01);
        message.setValue3(0x01L);

        netCommTemplate.convertAndSend(message);
    }
}
