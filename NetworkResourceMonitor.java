package org.example;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.util.List;

public class NetworkResourceMonitor {

    public static String getNetworkUsage() {
        SystemInfo systemInfo = new SystemInfo();
        List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();

        if (networkIFs.isEmpty()) {
            return "未找到网络接口信息。";
        }

        // 只获取第一个网络接口的数据
        NetworkIF net = networkIFs.get(0);
        net.updateAttributes();

        StringBuilder networkUsageInfo = new StringBuilder();
        networkUsageInfo.append("网络接口: ").append(net.getName()).append("\n");

        // 检查 IPv4 地址数组是否为空
        String[] ipv4Addresses = net.getIPv4addr();
        if (ipv4Addresses.length > 0) {
            networkUsageInfo.append("IP地址: ").append(ipv4Addresses[0]).append("\n");
        } else {
            networkUsageInfo.append("IP地址: 无\n");
        }

        networkUsageInfo.append("接收的字节数: ").append(net.getBytesRecv()).append("\n");
        networkUsageInfo.append("发送的字节数: ").append(net.getBytesSent()).append("\n");
        networkUsageInfo.append("接收的包数: ").append(net.getPacketsRecv()).append("\n");
        networkUsageInfo.append("发送的包数: ").append(net.getPacketsSent()).append("\n");

        return networkUsageInfo.toString();
    }
}
