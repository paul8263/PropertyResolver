import com.paultech.resolver.PropertyResolver;
import com.paultech.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

public class PropertyResolverTest {
    @Test
    public void test() throws Exception {
        long start = System.currentTimeMillis();
        Prop property = new PropertyResolver().getProperty(Prop.class);
        System.out.println(System.currentTimeMillis() - start);
        Assert.assertEquals("http://example.com", property.url);
        Assert.assertEquals("root", property.username);
        Assert.assertEquals("123456", property.jdbcPassword);
    }

    @Test
    public void testStringUtil() {
        String s = StringUtil.convertStringToCamelCase("kafka.data.source", "\\.");
        Assert.assertEquals("kafkaDataSource", s);
    }
}


