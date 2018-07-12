import com.paultech.annotation.AfterInjection;
import com.paultech.annotation.PropertyName;
import com.paultech.annotation.PropertySource;

import java.util.Properties;

@PropertySource(value = "src/test/resources/conf.properties")
public class Prop {
    String url;
    @PropertyName("jdbc.username")
    String username;

    String jdbcPassword;

    @AfterInjection
    private void method(Properties properties) {
        System.out.println(properties);
    }

    @AfterInjection
    private void another(String s) {
        System.out.println("another");
    }
}
