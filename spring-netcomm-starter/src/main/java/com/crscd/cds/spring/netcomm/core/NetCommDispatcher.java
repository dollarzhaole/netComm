package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.ctc.controller.InboundDispatcher;
import com.crscd.cds.ctc.protocol.NetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhaole
 * @date 2022-04-28
 */
public class NetCommDispatcher implements InboundDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetCommDispatcher.class);
    private final List<AbstractMessageListenerContainer> listenerContainers = new ArrayList<>();

    @Override
    public void dispatch(short type, short func, byte[] data, NetAddress srcAddress) throws Exception {
        Optional<AbstractMessageListenerContainer> containerOptional =
                listenerContainers.parallelStream()
                        .filter(
                                container ->
                                        container.getType() == type && container.getFunc() == func)
                        .findFirst();

        if (!containerOptional.isPresent()) {
            LOGGER.warn("no matched listener: {} {}", type, func);
            return;
        }

        AbstractMessageListenerContainer container = containerOptional.get();
        container.invokeListener(data, srcAddress);
    }

    public void addListener(AbstractMessageListenerContainer listenerContainer) {
        listenerContainers.add(listenerContainer);
    }
}
