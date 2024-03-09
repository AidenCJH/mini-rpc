package top.aidenchen.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取配置文件类
 */
public class PropertiesReader {
    private static final Map<String, String> myProperties;

    /**
     * 初始化读取配置文件
     */
    static {
        myProperties = new HashMap<>();
        Properties properties = new Properties();//获取Properties实例
        InputStream inStream = PropertiesReader.class.getResourceAsStream("/config.properties");//获取配置文件输入流
        try {
            properties.load(inStream);//载入输入流
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                myProperties.put(key, properties.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件错误!");
        }
    }

    /**
     * 获取配置信息
     * @param key
     * @return
     */
    public static String get(String key) {
        if (!myProperties.containsKey(key)) {
            throw new RuntimeException("配置文件中找不到配置项" + key);
        }
        return myProperties.get(key);
    }
}
