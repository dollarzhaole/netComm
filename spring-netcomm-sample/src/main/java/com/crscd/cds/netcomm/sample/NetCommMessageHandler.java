package com.crscd.cds.netcomm.sample;

import com.crscd.cds.spring.netcomm.annotation.NetCommListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhaole
 * @date 2022-04-28
 */
@Component
public class NetCommMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetCommMessageHandler.class);

    @NetCommListener(type = 49, func = 50)
    public void handle(RequestMessageContent message) {
        LOGGER.debug("handle from NetCommMessageHandler {}", message);
    }
}
