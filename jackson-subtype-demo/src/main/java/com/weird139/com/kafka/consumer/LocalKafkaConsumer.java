package com.weird139.com.kafka.consumer;

import com.weird139.com.model.Student;
import com.weird139.com.util.JacksonUtils;
import kafka.consumer.Consumer;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class LocalKafkaConsumer implements DisposableBean {
    private ConsumerConnector consumerConnector;
    @PostConstruct
    public void start() {
        if (consumerConnector == null) {
            log.info("start kafka consumer ...");
            Properties properties = new Properties();
            properties.setProperty("zookeeper.connect", "192.168.2.35:2181");
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.2.9:9092,192.168.2.9:9192");
            properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "fkh-iot-parser-group");
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "fkh-iot-parser-group-" + Thread.currentThread().getName());
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "smallest");
            properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

            consumerConnector = Consumer.createJavaConsumerConnector(new kafka.consumer.ConsumerConfig(properties));
            processMsg();
        }
    }

    private void processMsg() {
        // 处理消息内部类
        new ProcessMsgThread().run();
    }

    @Override
    public void destroy() {
        log.info("destroy kafka consumer...");
        if (consumerConnector != null) {
            try {
                consumerConnector.shutdown();
            }catch (Exception e) {
                log.error("kafka consumer close error", e);
            }
        }
    }

    /** 异步处理kafka消息 */
    class ProcessMsgThread implements Runnable {
        private ExecutorService executor = Executors.newFixedThreadPool(4);
        @Override
        public void run() {
            StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
            StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
            Map<String, Integer> topicsMap = new HashMap<>(1);
            topicsMap.put("kafka_tesk_topic", 1);
            Map<String, List<KafkaStream<String, String>>> messageStreams = consumerConnector.createMessageStreams(topicsMap, keyDecoder, valueDecoder);
            if (messageStreams != null) {
                for (Map.Entry entry : messageStreams.entrySet()) {
                    List<KafkaStream<String,  String>> value = (List<KafkaStream<String,  String>>) entry.getValue();
                    value.forEach(stream -> executor.submit(() -> {
                        for (MessageAndMetadata<String,  String> messageAndMetadata : stream) {
                            try {
                                String msg = messageAndMetadata.message();
                                log.info("--------------- msg source = {}", msg);
                                Student student = JacksonUtils.getObjectMapper().readValue(msg, Student.class);
                                log.info("--------------- msg dest = {}", student);
                            } catch (Exception e) {
                                log.error("handler kafka message error!", e);
                            }
                        }
                    }));
                }
            }
        }
    }
}
