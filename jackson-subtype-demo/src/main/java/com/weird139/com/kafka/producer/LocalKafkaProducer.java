package com.weird139.com.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weird139.com.model.Student;
import com.weird139.com.model.Study;
import com.weird139.com.util.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @author https://github.com/CD-ZHUBO
 */
@Slf4j
@Service
public class LocalKafkaProducer {

    private KafkaProducer kafkaProducer;

    @PostConstruct
    public void initService(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.2.9:9092");
        properties.put("client.id", "fkh-iot-parser-" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("retries", 1);
        properties.put("retry.backoff.ms", 500);

        kafkaProducer = new KafkaProducer(properties);
    }

    /** 消息发送 */
    public void send(ProducerRecord producerRecord) {
        kafkaProducer.send(producerRecord, new SendCallback(producerRecord, 0));
    }

    /** producer回调，做ack */
    class SendCallback implements Callback {
        ProducerRecord record;
        int sendSeq = 0;

        SendCallback(ProducerRecord record, int sendSeq) {
            this.record = record;
            this.sendSeq = sendSeq;
        }
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            //发送成功
            if (null == e) {
                String meta = "topic:" + recordMetadata.topic() + ", partition:"
                        + recordMetadata.topic() + ", offset:" + recordMetadata.offset();
                log.info("send message success, record:" + record.toString() + ", meta:" + meta);
                return;
            }
            //发送失败，需要再次尝试发送
            log.error("{}", e);
            log.error("send message failed, seq:" + sendSeq + ", record:" + record.toString() + ", errmsg:" + e.getMessage());
            if (sendSeq < 3) {
                kafkaProducer.send(record, new SendCallback(record, ++sendSeq));
            }
        }
    }
}
