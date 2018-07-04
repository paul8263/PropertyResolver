import com.paultech.annotation.PropertyName;
import com.paultech.annotation.PropertySource;

@PropertySource(value = "C:\\Users\\Paul\\Desktop\\tmp\\conf.properties")
public class Prop {
    String url;
    @PropertyName("jdbc.username")
    String username;

    String jdbcPassword;
}
