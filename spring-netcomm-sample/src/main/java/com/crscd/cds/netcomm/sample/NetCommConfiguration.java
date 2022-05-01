package com.crscd.cds.netcomm.sample;

import com.crscd.cds.spring.netcomm.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaole
 * @date 2022-05-01
 */
@Configuration
public class NetCommConfiguration {
    @Bean
    public MessageConverter messageConverter() {
        return new MessageContentConverter();
    }
}
