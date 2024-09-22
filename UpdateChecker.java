package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.json.JSONArray;

public class UpdateChecker {

    private static final Logger logger = LoggerFactory.getLogger(UpdateChecker.class);

    private static final String LOCAL_VERSION_FILE = "version.txt"; // 本地版本文件
    private static final String GITHUB_API_URL = "https://api.github.com/repos/你的仓库/releases/latest"; // 替换为你的GitHub API URL
    private static final String GITEE_API_URL = "https://gitee.com/api/v5/repos/你的仓库/releases/latest"; // 替换为你的Gitee API URL

    // 检查更新并下载
    public static void checkForUpdatesAndDownload() throws IOException {
        // 获取最新版本
        String latestVersion = getLatestVersion();
        String localVersion = getLocalVersion();

        if (!latestVersion.equals(localVersion)) {
            logger.info("检测到新版本: " + latestVersion + "，开始下载更新...");
            String downloadUrl = getLatestReleaseDownloadUrl();
            downloadNewVersion(downloadUrl);
            logger.info("新版本下载完成，准备重启...");
            restartApplication();
        } else {
            logger.info("当前已是最新版本: " + localVersion);
        }
    }

    // 从远程获取最新版本（GitHub 或 Gitee）
    public static String getLatestVersion() throws IOException {
        // 优先检查 GitHub
        String latestVersion = fetchVersionFromApi(GITHUB_API_URL);
        if (latestVersion == null || latestVersion.isEmpty()) {
            // 如果 GitHub 检查失败，检查 Gitee
            latestVersion = fetchVersionFromApi(GITEE_API_URL);
        }
        return latestVersion;
    }

    // 从API获取版本号
    private static String fetchVersionFromApi(String apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
                String response = new String(in.readAllBytes());
                JSONObject json = new JSONObject(response);
                return json.getString("tag_name");  // GitHub/Gitee 发布版本号通常保存在 tag_name 中
            }
        }
        return null;
    }

    // 获取最新发布的下载链接
    public static String getLatestReleaseDownloadUrl() throws IOException {
        // 优先检查 GitHub
        String downloadUrl = fetchDownloadUrlFromApi(GITHUB_API_URL);
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            // 如果 GitHub 检查失败，检查 Gitee
            downloadUrl = fetchDownloadUrlFromApi(GITEE_API_URL);
        }
        return downloadUrl;
    }

    // 从API获取最新发布的下载链接
    private static String fetchDownloadUrlFromApi(String apiUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
                String response = new String(in.readAllBytes());
                JSONObject json = new JSONObject(response);

                // 获取 GitHub 和 Gitee 发布资产 (assets) 的下载链接
                JSONArray assets = json.getJSONArray("assets");
                if (assets.length() > 0) {
                    JSONObject asset = assets.getJSONObject(0); // 获取第一个下载资源
                    return asset.getString("browser_download_url");
                }
            }
        }
        return null;
    }

    // 获取本地版本号
    public static String getLocalVersion() throws IOException {
        if (!Files.exists(Paths.get(LOCAL_VERSION_FILE))) {
            return "0.0.0"; // 如果没有本地版本文件，默认返回 0.0.0
        }
        return new String(Files.readAllBytes(Paths.get(LOCAL_VERSION_FILE))).trim();
    }

    // 下载新版本文件
    public static void downloadNewVersion(String fileUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream out = new FileOutputStream("new_version.zip")) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, count);
            }
        }
    }

    // 重启应用程序
    public static void restartApplication() throws IOException {
        String javaBin = System.getProperty("java.home") + "/bin/java";
        String jarPath = new java.io.File(UpdateChecker.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        ProcessBuilder builder = new ProcessBuilder(javaBin, "-jar", jarPath);
        builder.start();
        System.exit(0);
    }
}
