package top.aidenchen.protocol;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import top.aidenchen.common.Invocation;
import top.aidenchen.properties.PropertiesReader;
import top.aidenchen.register.LocalRegister;

import java.io.ObjectInputStream;
import java.lang.reflect.Method;

public class HttpServerHandler {
    public void handler(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (PropertiesReader.get("server_log_work").equals("True")) {
                System.out.println("收到来自" + req.getRemoteHost() + ":" + req.getRemotePort() + "的调用");
            }
            Invocation invocation = (Invocation) new ObjectInputStream(req.getInputStream()).readObject();
            String interfaceName = invocation.getInterfaceName();
            Class implClass = LocalRegister.get(interfaceName);
            Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            Object result = method.invoke(implClass.newInstance(), invocation.getParameters());
            IOUtils.write(JSON.toJSONString(result), resp.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
