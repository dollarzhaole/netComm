package com.crscd.cds.spring.netcomm.config;

import com.crscd.cds.ctc.client.DoubleClient;
import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.protocol.NetAddress;
import com.crscd.cds.spring.netcomm.core.NetCommDispatcher;
import com.crscd.cds.spring.netcomm.core.NetCommListenerAnnotationBeanPostProcessor;
import com.crscd.cds.spring.netcomm.core.SimpleNetCommListenerContainerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaole
 * @date 2022-04-24
 */
@Configuration
@ConditionalOnClass({NettyClient.class, DoubleClient.class})
@EnableConfigurationProperties(NetCommProperties.class)
public class NetCommAutoConfiguration {
    @Bean
    public DoubleClient client(NetCommProperties commProperties, NetCommDispatcher netCommDispatcher) {
        NetCommProperties.LocalAddress local = commProperties.getLocal();
        NetAddress localAddress = NetAddress.create(local.getBureauCode(), local.getUnitType().getValue(), local.getUnitId());

        List<FilterRegister.TypeFunc> typeFuncList = commProperties.getInCondition().getRec().stream()
                .map(tf -> FilterRegister.TypeFunc.create(tf.getType(), tf.getFunc()))
                .collect(Collectors.toList());

        FilterRegister register = FilterRegister.create(typeFuncList, FilterRegister.ClientAddress.create(localAddress));

        NettyClient client1 = client1(commProperties, localAddress, register, netCommDispatcher);
        NettyClient client2 = client2(commProperties, localAddress, register);

        DoubleClient client = new DoubleClient(client1, client2);
        client.start();

        return client;
    }

    @Bean
    public NetCommTemplate netCommTemplate(DoubleClient client) {
        return new NetCommTemplate(client);
    }

    @Bean
    public SimpleNetCommListenerContainerFactory netCommListenerContainerFactory(NetCommDispatcher netCommDispatcher) {
        SimpleNetCommListenerContainerFactory factory = new SimpleNetCommListenerContainerFactory();
        factory.setDispatcher(netCommDispatcher);
        return factory;
    }

    private NettyClient client1(NetCommProperties properties, NetAddress localAddress, FilterRegister register, NetCommDispatcher dispatcher) {
        NetCommProperties.Server server1 = properties.getServer1();
        return new NettyClient(server1.getHost(), server1.getPort(), localAddress, register, dispatcher);
    }

    @Nullable
    private NettyClient client2(NetCommProperties properties, NetAddress localAddress, FilterRegister register) {
        return null;
//        NetCommProperties.Server server = properties.getServer2();
//        if (server == null) {
//            return null;
//        }
//
//        return new NettyClient(server.getHost(), server.getPort(), localAddress, register);
    }

    @Bean
    public NetCommListenerAnnotationBeanPostProcessor netCommListenerAnnotationBeanPostProcessor() {
        return new NetCommListenerAnnotationBeanPostProcessor();
    }

    @Bean
    public NetCommDispatcher netCommDispatcher() {
        return new NetCommDispatcher();
    }
}
