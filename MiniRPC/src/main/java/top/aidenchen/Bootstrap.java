package top.aidenchen;

import top.aidenchen.properties.PropertiesReader;
import top.aidenchen.protocol.HttpServer;
import top.aidenchen.register.LocalRegister;
import top.aidenchen.utils.JedisUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class MyThread extends Thread {
    private static List<String> interfaceNames;
    private String hostAddress;
    private Integer port;
    private Integer weight;

    @Override
    public void run() {
        while (true) {
            String msg = hostAddress + ":" + port + ":" + weight;
            String sign = PropertiesReader.get("redis_sign");
            for (String interfaceName : interfaceNames) {
                JedisUtils.set(sign + ":" + interfaceName + ":" + msg, msg);
            }
//            System.out.println("心跳机制执行了一次...");
            try {
                sleep(1000L * Integer.parseInt(PropertiesReader.get("server_heartbeat_interval")));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static {
        interfaceNames = new ArrayList<>();
    }

    public static void register(String interfaceName) {
        interfaceNames.add(interfaceName);
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}

public class Bootstrap {
    public static void register(String interfaceName, Class implClass) {
        MyThread.register(interfaceName);
        LocalRegister.register(interfaceName, implClass);
    }

    public static void start(Integer port, Long weight) {
        if (isLocalPortUsing(port)) {
            throw new RuntimeException("端口:" + port + "已被占用");
        }
        String hostAddress = getLocalHostExactAddress().getHostAddress();
        MyThread myThread = new MyThread();
        myThread.setHostAddress(hostAddress);
        myThread.setPort(port);
        myThread.setWeight(Math.toIntExact(weight));
        myThread.start();
        System.out.println("启动服务!" + "IP:" + hostAddress + " 端口:" + port + " 权重:" + weight);
        HttpServer httpServer = new HttpServer();
        httpServer.start(hostAddress, port);
    }

    public static void start(Long weight) {
        int port = Integer.parseInt(PropertiesReader.get("server_default_port"));
        while (isLocalPortUsing(port)) {
            port++;
        }
        start(port, weight);
    }

    public static void start(Integer port) {
        Long weight = Long.parseLong(PropertiesReader.get("server_default_weight"));
        start(port, weight);
    }

    public static void start() {
        Long weight = Long.parseLong(PropertiesReader.get("server_default_weight"));
        start(weight);
    }

    public static boolean isLocalPortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {
            //如果所测试端口号没有被占用，那么会抛出异常，这里利用这个机制来判断
            //所以，这里在捕获异常后，什么也不用做
        }
        return flag;
    }

    public static InetAddress getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }

                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
