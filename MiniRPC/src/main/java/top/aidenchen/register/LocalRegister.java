package top.aidenchen.register;

import java.util.HashMap;
import java.util.Map;

public class LocalRegister {
    /**
     * 储存接口和实现类的对应关系
     */
    private static final Map<String, Class> implMap = new HashMap<>();

    /**
     * 新增接口和实现类对应关系
     *
     * @param interfaceName
     * @param implClass
     */
    public static void register(String interfaceName, Class implClass) {
        implMap.put(interfaceName, implClass);
    }

    /**
     * 获取接口和实现类对应关系
     *
     * @param interfaceName
     * @return
     */
    public static Class get(String interfaceName) {
        return implMap.get(interfaceName);
    }
}
