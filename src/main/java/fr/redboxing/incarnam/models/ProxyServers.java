package fr.redboxing.incarnam.models;

import com.google.gson.annotations.SerializedName;

public class ProxyServers {
    @SerializedName("uber")
    private ProxyServer uber;

    public ProxyServers(ProxyServer uber) {
        this.uber = uber;
    }

    public ProxyServer getUber() {
        return this.uber;
    }

    public void setUber(ProxyServer value) {
        this.uber = value;
    }

    public String toString() {
        return "ProxyServers{m_uber=" + this.uber + '}';
    }
}
