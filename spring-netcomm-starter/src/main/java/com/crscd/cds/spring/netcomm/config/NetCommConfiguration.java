package com.crscd.cds.spring.netcomm.config;

import com.crscd.cds.ctc.client.DoubleClient;
import com.crscd.cds.ctc.client.NettyClient;
import com.crscd.cds.ctc.filter.FilterRegister;
import com.crscd.cds.ctc.protocol.NetAddress;
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
@Import(NetCommProperties.class)
public class NetCommConfiguration {
    @Bean
    public DoubleClient client(NetCommProperties commProperties) {
        NetCommProperties.LocalAddress local = commProperties.getLocal();
        NetAddress localAddress = NetAddress.create(local.getBureauCode(), local.getUnitType().getValue(), local.getUnitId());

        List<FilterRegister.TypeFunc> typeFuncList = commProperties.getInCondition().getRec().stream()
                .map(tf -> FilterRegister.TypeFunc.create(tf.getType(), tf.getFunc()))
                .collect(Collectors.toList());

        FilterRegister register = FilterRegister.create(typeFuncList, FilterRegister.ClientAddress.create(localAddress));

        NettyClient client1 = client1(commProperties, localAddress, register);
        NettyClient client2 = client2(commProperties, localAddress, register);

        DoubleClient client = new DoubleClient(client1, client2);
        client.start();

        return client;
    }

    private NettyClient client1(NetCommProperties properties, NetAddress localAddress, FilterRegister register) {
        NetCommProperties.Server server1 = properties.getServer1();
        return new NettyClient(server1.getHost(), server1.getPort(), localAddress, register);
    }

    @Nullable
    private NettyClient client2(NetCommProperties properties, NetAddress localAddress, FilterRegister register) {
        NetCommProperties.Server server = properties.getServer2();
        if (server == null) {
            return null;
        }

        return new NettyClient(server.getHost(), server.getPort(), localAddress, register);
    }

}
