# study
### jackson-subtype-demo 
    当我们用jackson框架进行反序列化时，如果对象的成员为接口，且该接口拥有大于1个的实现类时，直接使用ObjectMapper的readValue时常会出现异常，此时需要借助注解：JsonSubTypes来实现反序列化。
