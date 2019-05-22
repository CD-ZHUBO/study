package com.weird139.com.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weird139.com.kafka.producer.LocalKafkaProducer;
import com.weird139.com.model.Student;
import com.weird139.com.model.Study;
import com.weird139.com.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class KafkaProducerDemo {
    @Autowired
    private LocalKafkaProducer producer;

    @PostConstruct
    public void sendMsg() {
        log.info("ready to send message to kafka.");
        // 测试使用，默认发送一条数据
        Student student = Student.builder()
                .name("jackson subtype test")
                .behaviour(Study.builder().behaviourName("study hebaviour").build())
                .build();
        try {
            ProducerRecord producerRecord = new ProducerRecord("kafka_tesk_topic", JacksonUtils.getObjectMapper().writeValueAsString(student));
            producer.send(producerRecord);
            log.info("send kafka message [{}] success.", producerRecord);
        } catch (JsonProcessingException e) {
            log.error("send kafka message error.", e);
        }
    }
}
