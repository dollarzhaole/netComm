package com.crscd.cds.spring.netcomm.config;

import com.crscd.cds.ctc.client.DoubleClient;
import com.crscd.cds.ctc.client.NetCommClient;
import com.crscd.cds.ctc.enums.ClientFlagEnum;
import com.crscd.cds.ctc.forward.FilterRegister;
import com.crscd.cds.ctc.protocol.NetAddress;
import com.crscd.cds.spring.netcomm.converter.MessageConverter;
import com.crscd.cds.spring.netcomm.core.NetCommDispatcher;
import com.crscd.cds.spring.netcomm.core.NetCommListenerAnnotationBeanPostProcessor;
import com.crscd.cds.spring.netcomm.core.SimpleNetCommListenerContainerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author zhaole
 * @date 2022-04-24
 */
@Configuration
@ConditionalOnClass({NetCommClient.class, DoubleClient.class})
@EnableConfigurationProperties(NetCommProperties.class)
public class NetCommAutoConfiguration {
    @Bean(initMethod = "start", destroyMethod = "close")
    public DoubleClient client(
            NetCommProperties commProperties, NetCommDispatcher netCommDispatcher) {
        NetCommProperties.LocalAddress local = commProperties.getLocal();
        NetAddress localAddress =
                NetAddress.create(
                        local.getBureauCode(),
                        local.getUnitType().getValue(),
                        local.getUnitId(),
                        local.getClientId());

        List<FilterRegister.TypeFunc> typeFuncList =
                commProperties.getInCondition().getRec().stream()
                        .map(tf -> FilterRegister.TypeFunc.create(tf.getType(), tf.getFunc()))
                        .collect(Collectors.toList());

        FilterRegister register =
                FilterRegister.create(
                        typeFuncList, FilterRegister.ClientAddress.create(localAddress));

        NetCommClient client1 = client1(commProperties, localAddress, register, netCommDispatcher);
        NetCommClient client2 = client2(commProperties, localAddress, register, netCommDispatcher);

        return new DoubleClient(client1, client2);
    }

    @Bean
    public NetCommTemplate netCommTemplate(DoubleClient client, MessageConverter messageConverter) {
        return new NetCommTemplate(client, messageConverter);
    }

    @Bean
    @ConditionalOnMissingBean(Executor.class)
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        return executor;
    }

    @Bean
    public SimpleNetCommListenerContainerFactory netCommListenerContainerFactory(
            NetCommDispatcher netCommDispatcher,
            MessageConverter messageConverter,
            Executor executor) {
        SimpleNetCommListenerContainerFactory factory = new SimpleNetCommListenerContainerFactory();
        factory.setDispatcher(netCommDispatcher);
        factory.setMessageConverter(messageConverter);
        factory.setTaskExecutor(executor);
        return factory;
    }

    private NetCommClient client1(
            NetCommProperties properties,
            NetAddress localAddress,
            FilterRegister register,
            NetCommDispatcher dispatcher) {
        NetCommProperties.Server server1 = properties.getServer1();
        return new NetCommClient(
                server1.getHost(),
                server1.getPort(),
                server1.getLocalPort(),
                localAddress,
                register,
                dispatcher,
                ClientFlagEnum.NET1);
    }

    @Nullable
    private NetCommClient client2(
            NetCommProperties properties,
            NetAddress localAddress,
            FilterRegister register,
            NetCommDispatcher dispatcher) {
        NetCommProperties.Server server = properties.getServer2();
        if (server == null) {
            return null;
        }

        return new NetCommClient(
                server.getHost(),
                server.getPort(),
                server.getLocalPort(),
                localAddress,
                register,
                dispatcher,
                ClientFlagEnum.NET2);
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
