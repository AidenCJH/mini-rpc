package top.aidenchen;

import top.aidenchen.protocol.HttpServer;
import top.aidenchen.register.LocalRegister;

public class Provider {
    public static void main(String[] args) {
        Bootstrap.register(TestService.class.getName(), TestServiceImpl.class);
        Bootstrap.start();
    }
}
