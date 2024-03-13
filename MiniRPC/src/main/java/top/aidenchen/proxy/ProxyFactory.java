package top.aidenchen.proxy;

import com.alibaba.fastjson2.JSON;
import top.aidenchen.common.Invocation;
import top.aidenchen.common.URL;
import top.aidenchen.loadbalance.LoadBalance;
import top.aidenchen.properties.PropertiesReader;
import top.aidenchen.protocol.HttpClient;
import top.aidenchen.remote.netty.client.NettyClient;
import top.aidenchen.utils.JedisUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProxyFactory {
    public static volatile Object result;

    public static <T> T getProxy(Class interfaceClass) {
        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation(interfaceClass.getName(),
                        method.getName(), method.getParameterTypes(),
                        args);
                String sign = PropertiesReader.get("redis_sign");
                Set<String> keys = JedisUtils.keys(sign + ":" + interfaceClass.getName() + "*");
                if (keys.isEmpty()) {
                    throw new RuntimeException("没有可调用的远程方法!");
                }
                List<URL> servers = new ArrayList<>();
                for (String key : keys) {
                    String[] split = key.split(":");
                    URL url = new URL(split[split.length - 3], Integer.parseInt(split[split.length - 2]), Integer.parseInt(split[split.length - 1]));
                    servers.add(url);
                }
                URL url = LoadBalance.loadbalance(servers);
                boolean success = false;
                int retry_num = Integer.parseInt(PropertiesReader.get("client_retry_num"));
                while (retry_num > 0) {
                    retry_num--;
                    try {
                        result = null;
                        NettyClient.run(url.getHostAddress(), url.getPort(), invocation);
                        success = true;
                        break;
                    } catch (Exception e) {
                        servers.remove(url);
                        if (servers.isEmpty()) {
                            throw new RuntimeException("没有可调用的远程方法!");
                        }
                        url = LoadBalance.loadbalance(servers);
                    }
                }
                if (!success) {
                    throw new RuntimeException("没有可调用的远程方法!");
                }
                return result;
            }
        });
        return (T) proxyInstance;
    }
}
