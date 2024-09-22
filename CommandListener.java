package org.example;

import java.util.Scanner;

public class CommandListener {

    // 监听命令行输入
    public static void listenForCommands() throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请输入命令：");
            String command = scanner.nextLine();

            if ("/info".equals(command)) {
                // 调用获取系统资源占用信息的方法
                String resourceInfo = ResourceMonitorAndNotifier.getSystemResourceInfo();

                // 调用企业微信机器人发送通知
                WeChatNotifier.sendNotification(resourceInfo);

                System.out.println("系统资源信息已发送至企业微信。");
            } else {
                System.out.println("无效命令，请输入 /info 获取系统资源占用信息。");
            }
        }
    }
}
