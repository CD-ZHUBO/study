package com.weird139.com.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class Work implements Behaviour {
    private int workYear;
    @Override
    public void say() {
        log.info("hello work");
    }
}
