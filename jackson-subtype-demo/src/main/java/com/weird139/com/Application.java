package com.weird139.com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weird139.com.model.Student;
import com.weird139.com.model.Study;
import com.weird139.com.util.JacksonUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
