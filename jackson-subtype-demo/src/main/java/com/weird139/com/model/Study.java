package com.weird139.com.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
public class Study implements Behaviour {
    private String behaviourName;

    @Override
    public void say() {
        log.info("hello study!");
    }
}
