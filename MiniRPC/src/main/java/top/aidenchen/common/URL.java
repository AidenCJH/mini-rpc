package top.aidenchen.common;

public class URL {
    private String hostAddress;
    private Integer port;
    private Integer weight;

    public URL(String hostAddress, Integer port, Integer weight) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.weight = weight;
    }

    public URL() {
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
