when we use jackson to deserialize object which contains interface field，it will not work properly using ObjectMapper only。
But, we can user annotation of JsonSubTypes to fix it.
当我们用jackson框架进行反序列化时，如果对象的成员为接口，且该接口拥有大于1个的实现类时，
直接使用ObjectMapper的readValue时常会出现异常，此时需要借助注解：JsonSubTypes来实现反序列化。