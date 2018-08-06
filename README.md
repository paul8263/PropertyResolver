# Property Resolver Project

# Introduction

The way to ease the pain of reading external property files.

Basic Usage:

```properties
username=paul
password=p@ssw0rd
``` 

```java
@PropertySource("path/to/propertyFile.properties")
class PropertyBean {
    String username;
    String password;
}

class Demo {
    public static void main(String[] args){
      PropertyBean property = new PropertyResolver().getProperty(PropertyBean.class);
      
      property.username; // paul     
      property.password; // p@ssw0rd
    }
}

```

# More examples

1. Convert dot/dash separated property name to camel case.

**Dot**

Property file:

```properties
my.username=paul
my.password=p@ssw0rd
``` 

Java code:

```java
@PropertySource(fieldNamingPolicy = FieldNamingPolicy.DOT_SEPARATED_TO_CAMEL_CASE, value = "path/to/propertyFile.properties")
class PropertyBean {
    String myUsername; // Convert my.username to myUsername
    String myPassword; // Convert my.password to myPassword
}
```

**Dash**

Property file:

```properties
my-username=paul
my-password=p@ssw0rd
``` 

Java code:

```java
@PropertySource(fieldNamingPolicy = FieldNamingPolicy.DASH_SEPARATED_TO_CAMEL_CASE, value = "path/to/propertyFile.properties")
class PropertyBean {
    String myUsername; // Convert my-username to myUsername
    String myPassword; // Convert my-password to myPassword
}
```

If we use `FieldNamingPolicy.EXACTLY_MATCH`, there will be no naming conversion for property names.

2. Define custom property name.

Property file:

```properties
jdbc.username=paul
jdbc.password=password
```

Java code:

```java
@PropertySource(fieldNamingPolicy = FieldNamingPolicy.DOT_SEPARATED_TO_CAMEL_CASE, value = "path/to/propertyFile.properties")
class PropertyBean {
    @PropertyName("jdbc.username")
    String username; // Refer to the exact property name "jdbc.username" in the property file
    String jdbcPassword; // Convert my.password to myPassword by default
}
```

3. Do extra things after reading properties.

```java
public class Prop {
    String url;
    @PropertyName("jdbc.username")
    String username;

    String jdbcPassword;
    
    @AfterInjection
    private void basicExample() {
        System.out.println("Do something");
        jdbcPassword = decrypt(jdbcPassword);
    }

    @AfterInjection
    private void anotherExample(Properties properties) { // You may use Properties parameter if you want to read raw properties.
        System.out.println(properties);
    }
    
    private String decrypt(String original) {
        ...
    }

}
```

# Author

Paul Zhang