package com.weird139.com.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Study.class, name = "Study"),
        @JsonSubTypes.Type(value = Work.class, name = "Work")})
public interface Behaviour extends Serializable {
    public void say();
}
