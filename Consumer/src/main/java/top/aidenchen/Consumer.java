package top.aidenchen;

import top.aidenchen.proxy.ProxyFactory;

public class Consumer {
    public static void main(String[] args) {
        TestService testService = ProxyFactory.getProxy(TestService.class);
        System.out.println(testService.sayHello("cjh"));
    }
}
