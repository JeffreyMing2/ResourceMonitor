package org.example;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResourceMonitorAndNotifier {

    private static final SystemInfo systemInfo = new SystemInfo();
    private static final Logger logger = LoggerFactory.getLogger(ResourceMonitorAndNotifier.class);

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 启动定时任务监控
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String resourceInfo = getSystemResourceInfo();
                WeChatNotifier.sendNotification(resourceInfo);
                logger.info("系统资源信息已发送。");
            } catch (Exception e) {
                logger.error("定时任务监控出错", e);
            }
        }, 0, 5, TimeUnit.HOURS);

        // 监听命令行输入
        CommandListener.listenForCommands();
    }

    // 获取系统资源占用情况并生成信息字符串
    public static String getSystemResourceInfo() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        // 获取 CPU 和内存占用
        double cpuLoad = processor.getSystemCpuLoad(1000) * 100;
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        double memoryUsage = 100.0 * (totalMemory - availableMemory) / totalMemory;

        // 获取网络占用情况
        String networkUsage = NetworkResourceMonitor.getNetworkUsage();

        // 生成信息
        return String.format(
                "当前系统资源占用情况：\nCPU占用率: %.2f%%\n内存占用率: %.2f%%\n%s",
                cpuLoad, memoryUsage, networkUsage
        );
    }
}
