package com.sascom.chickenstock.global.config;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
public class RedisConfig {

    @Value("${redis.host}") // application.properties에서 필요한것들 매핑
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.nodes}")
    private List<String> nodes;

    // 둘다 스프링 Bean 컨테이너에 등록하여 단일 객체를 주입받아서 사용하도록 설정한다.
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
//        final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(host); // 가져와서 쓰기
//        redisStandaloneConfiguration.setPort(port);

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(nodes);
        redisClusterConfiguration.setMaxRedirects(3);

        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .dynamicRefreshSources(true)
                .enableAllAdaptiveRefreshTriggers()
                .enablePeriodicRefresh(Duration.ofHours(1))
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .build();

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clusterClientOptions)
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();

        LettuceConnectionFactory lettuceConnectionFactory =
                new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);

        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        // Redis에 저장할 데이터형식을 제공한다.
        // 어떤 connection을 사용하는지 명시하고
        // key, value에 대한 어떤 직렬화를 사용할 것인지 명시한다.
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

}