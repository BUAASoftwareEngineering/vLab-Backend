package org.nocturne.vslab.backend.util;

import java.util.HashMap;
import java.util.Map;

public class CloudFileHelper {

    public static void downloadFile(String ip, Integer projectId) {
        Map<String, String> params = new HashMap<>();
        params.put("projectId", String.valueOf(projectId));

        try {
            new HttpSender(ip, 6000, "/download", params).getForString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile(String ip, Integer projectId) {
        Map<String, String> params = new HashMap<>();
        params.put("projectId", String.valueOf(projectId));

        try {
            new HttpSender(ip, 6000, "/upload", params).postForString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteRemoteFile(Integer projectId) {
        Map<String, String> params = new HashMap<>();
        params.put("projectId", String.valueOf(projectId));

        try {
            new HttpSender("120.53.27.31", 6000, "/delete", params).postForString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteRemoteFile(String ip, Integer projectId) {
        Map<String, String> params = new HashMap<>();
        params.put("projectId", String.valueOf(projectId));

        try {
            new HttpSender(ip, 6000, "/delete", params).postForString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
