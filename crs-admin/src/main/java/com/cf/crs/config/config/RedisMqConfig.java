package com.cf.crs.config.config;

import com.cf.crs.listener.MyRedisConsumer;
import com.cf.util.utils.Const;
import io.lettuce.core.XGroupCreateArgs;
import io.lettuce.core.XReadArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConverters;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import javax.annotation.Resource;
import java.time.Duration;

@Slf4j
@Configuration
public class RedisMqConfig {
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private MyRedisConsumer myRedisConsumer;

    private static final String DEFAULT_TOPIC = Const.REDIS_STREAM_TOPIC;
    private static final String DEFAULT_GROUP = DEFAULT_TOPIC;

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> listener(RedisConnectionFactory connectionFactory) {
        //初始化topic
        try {
            LettuceConnection clusterConnection = (LettuceConnection) connectionFactory.getConnection();
            XReadArgs.StreamOffset<byte[]> streamOffset = XReadArgs.StreamOffset.from(LettuceConverters.toBytes(DEFAULT_GROUP), "0-0");
            clusterConnection.getNativeConnection().xgroupCreate(streamOffset, LettuceConverters.toBytes(DEFAULT_GROUP), XGroupCreateArgs.Builder.mkstream());
        } catch (Exception ex) {
            log.warn("Already Created {}", ex.getMessage());
        }


        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ZERO)
                .batchSize(1)
                .targetType(String.class)
                .executor(taskExecutor)
                .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container = StreamMessageListenerContainer
                .create(connectionFactory, options);


        //指定消费者对象
        container.register(
                StreamMessageListenerContainer.StreamReadRequest.builder(StreamOffset.create(DEFAULT_TOPIC, ReadOffset.lastConsumed()))
                        .errorHandler((error) -> {
                            if (!(error instanceof QueryTimeoutException)) {
                                log.error(error.getMessage(), error);
                            }
                        })
                        .cancelOnError(e -> false)
                        .consumer(Consumer.from(DEFAULT_GROUP, DEFAULT_GROUP))
                        //关闭自动ack确认
                        .autoAcknowledge(false)
                        .build()
                , myRedisConsumer);
        container.start();
        return container;
    }
}
