package com.weird139.com.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Student implements Serializable {

    private String name;
    private Behaviour behaviour;
}
