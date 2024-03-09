package top.aidenchen.loadbalance;

import top.aidenchen.common.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LoadBalance {
    public static URL loadbalance(List<URL> servers) {
        Map<Integer, URL> map = new HashMap<>();
        int num = 0;
        for (URL server : servers) {
            for (int i = 0; i < server.getWeight(); i++) {
                num++;
                map.put(num, server);
            }
        }
        Random random = new Random();
        return map.get(random.nextInt(num) + 1);
    }
}
