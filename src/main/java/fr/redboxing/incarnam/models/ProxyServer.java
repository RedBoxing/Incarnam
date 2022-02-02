package fr.redboxing.incarnam.models;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class ProxyServer {
    @SerializedName("address")
    private String address;

    @SerializedName("ports")
    private int[] ports;

    public ProxyServer(String address, int... ports) {
        this.address = address;
        this.ports = ports;
    }

    public String getAddress() {
        return this.address;
    }

    public int[] getPorts() {
        return this.ports.clone();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPorts(int... ports) {
        this.ports = ports;
    }

    public String toString() {
        return "ProxyServer{m_address='" + this.address + '\'' + ", m_ports=" + Arrays.toString(this.ports) + '}';
    }
}
