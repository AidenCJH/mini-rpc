package top.aidenchen.utils;

import redis.clients.jedis.Jedis;
import top.aidenchen.properties.PropertiesReader;

import java.util.Objects;
import java.util.Set;

/**
 * 操作Redis工具类
 */
public class JedisUtils {
    private static final Jedis jedis;

    /**
     * 初始化Redis连接
     */
    static {
        try {
            jedis = new Jedis(PropertiesReader.get("redis_host"), Integer.parseInt(PropertiesReader.get("redis_port")));
            if (Objects.equals(PropertiesReader.get("redis_requirePass"), "true")) {
                jedis.auth(PropertiesReader.get("redis_password"));
            }
        }catch (Exception e){
            throw new RuntimeException("连接Redis失败");
        }
    }

    /**
     * Redis设置kv
     *
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        jedis.set(key, value);
        jedis.expire(key, Integer.parseInt(PropertiesReader.get("server_heartbeat_expire")));
    }

    /**
     * Redis批量获取keys
     *
     * @param key
     * @return
     */
    public static Set<String> keys(String key) {
        return jedis.keys(key + "*");
    }
}
