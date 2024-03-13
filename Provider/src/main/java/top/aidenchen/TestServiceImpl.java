package top.aidenchen;

public class TestServiceImpl implements TestService {
    /**
     * （测试）在name前面加上hello:
     *
     * @param name
     * @return
     */
    @Override
    public String sayHello(String name) {
        return "hello: " + name;
    }

    @Override
    public Integer add(int a, int b) {
        return a + b;
    }

}
