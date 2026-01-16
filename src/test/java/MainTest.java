import com.bafmc.bukkit.utils.ExpUtils;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainTest {
    @Test
    public void onTest() {
        List<String> colors = List.of(
                "&#FF0000", "&#FF1E00", "&#FF3C00", "&#FF5A00",
                "&#FF7800", "&#FF9600", "&#FFB400", "&#FF8D00",
                "&#F1FE00", "&#F0FF00", "&#C3FF00", "&#96FF00",
                "&#69FF00", "&#3CFF00", "&#00FF1E", "&#00FF3C",
                "&#00FF5A", "&#00FF78", "&#00FF96", "&#00FFB4",
                "&#00FFD2", "&#00E9E8", "&#00D3FE", "&#00D2FF",
                "&#00A5FF", "&#0078FF", "&#004BFF", "&#001EFF",
                "&#3C00FF", "&#5A00FF", "&#7800FF", "&#9600FF",
                "&#B400FF", "&#D200FF", "&#F000FF", "&#F700DA",
                "&#FE00B5", "&#FF00B4", "&#FF0087", "&#FF005A",
                "&#FF002D"
        );

        String text = "Vòng quay may mắn";

        List<String> frames = generateFrames(colors, text);

// debug
        frames.forEach(str -> {
            System.out.println("- \"" + str + "\"");
        });
    }

    public static List<String> generateFrames(List<String> colors, String text) {
        List<String> frames = new ArrayList<>();

        int colorCount = colors.size();
        int textLength = text.length();

        for (int frame = 0; frame < colorCount; frame++) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < textLength; i++) {
                // 🔥 MÀU CUỐI → LÊN ĐẦU (đúng ví dụ gốc của bạn)
                int colorIndex = (colorCount - frame + i) % colorCount;
                String color = colors.get(colorIndex);

                sb.append(color).append("&l").append(text.charAt(i));
            }

            frames.add(sb.toString());
        }

        return frames;
    }

//    @Test
//    public void onTest() {
//        Map<Integer, Integer> levelCountMap = new LinkedHashMap<>();
//
//        double sigma = 0.3;
//
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//
//        System.out.println("Open 100 Books");
//        for (int i = 0; i < 100; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//        System.out.println(levelCountMap);
//        System.out.println("Open 1000 Books");
//        levelCountMap.clear();
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//        for (int i = 0; i < 1000; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//        System.out.println(levelCountMap);
//        System.out.println("Open 10000 Books");
//        levelCountMap.clear();
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//        for (int i = 0; i < 10000; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//
//
//        System.out.println(levelCountMap);
//
//        System.out.println(levelCountMap);
//        System.out.println("Open 100000 Books");
//        levelCountMap.clear();
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//        for (int i = 0; i < 100000; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//
//
//        System.out.println(levelCountMap);
//    }
//
    @Test
    public void onLevel() {
        for (int i = 1; i < 50; i++) {
            System.out.println(i * 80 + " " + ExpUtils.getExpToLevel(i * 80));
        }
    }

    @Test
    public void onMask() {
        for (EntityType entityType : EntityType.values()) {
            System.out.println("  - '" + entityType.name() + "'");
        }
//        File file = new File("src/test/resources/mask.yml");
//        AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);
//
//        for (String key : config.getKeys(false)) {
//            String path = key + ".levels";
//
//            try {
//                for (String level : config.getConfigurationSection(path).getKeys(false)) {
//                    String levelPath = path + "." + level;
//
//                    for (String functionName : config.getConfigurationSection(levelPath).getKeys(false)) {
//                        String functionPath = levelPath + "." + functionName;
//
//                        String typePath = functionPath + ".type";
//                        String type = config.getString(typePath);
//
//                        if (!type.equals("ARMOR_EQUIP") && !type.equals("ARMOR_UNDRESS") && !type.equals("HOLD") && !type.equals("CHANGE_HAND")) {
//                            List<String> conditions = config.getStringList(functionPath + ".conditions");
//                            if (conditions.isEmpty()) {
//                                conditions.add("EQUIP_SLOT:MAINHAND");
//                            } else if (!conditions.contains("EQUIP_SLOT:MAINHAND")) {
//                                conditions.add(0, "EQUIP_SLOT:MAINHAND");
//                            }
//
//                            // remove empty
//                            conditions.removeIf(String::isEmpty);
//
//                            config.set(functionPath + ".conditions", conditions);
//                        }
//                    }
//                }
//            } catch (NullPointerException e) {
//                System.out.println("Failed to load " + path);
//            }
//        }
//
//        config.save();
    }

    public static void main(String[] args) {
//        for (int i = 0; i < 999; i++) {
//            String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
//            System.out.println(timestamp);
//        }
        int[] arr = {3,2,1};

        Arrays.sort(arr);

        for (int i : arr) {
            System.out.println(i);
        }

//        // File download URL
//        String fileURL = "https://api-sdm-di.cfapps.ap21.hana.ondemand.com/browser/ZDMS_BRIMDMSNEW/root?repository_id=ZDMS_BRIMDMSNEW&cmisselector=content&download=attachment&objectId=oGOihGJA9KmE_iTV2QZwfCUSOV1WELXclCULT9fj15w";
//
//        // Authorization Token
//        String bearerToken = "Bearer eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYnRxLWMyM3ppaG9pLmF1dGhlbnRpY2F0aW9uLmFwMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS1jOWE2NDEyYjA5IiwidHlwIjoiSldUIiwiamlkIjogImdhU0ZiaUhTSkRWdWlnL0t3TkVtbGdLNi9sUjAxNENMWXh3cm5UNTcrSjA9In0.eyJqdGkiOiIxNzBlYTBjZGQxNTk0YmJhODBhZjI0ZDJhMWE1OGY1MiIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiI1ZGE1OGFlNS05Mzg0LTQzMzQtOWM2Yi1mYTZjYjBlNDFlMDgiLCJ6ZG4iOiJidHEtYzIzemlob2kiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6IjU2ZTM3MjA1LWJhOTYtNGQwNC05YzdkLTNhMThmMDg4MTRmMSJ9LCJzdWIiOiJzYi01NmUzNzIwNS1iYTk2LTRkMDQtOWM3ZC0zYTE4ZjA4ODE0ZjEhYjI1NTcyfHNkbS1kaS1ET0NVTUVOVE1BTkFHRU1FTlQtc2RtX2ludGVncmF0aW9uIWIxNTQiLCJhdXRob3JpdGllcyI6WyJzZG0tZGktRE9DVU1FTlRNQU5BR0VNRU5ULXNkbV9pbnRlZ3JhdGlvbiFiMTU0LnNkbW1pZ3JhdGlvbmFkbWluIiwic2RtLWRpLURPQ1VNRU5UTUFOQUdFTUVOVC1zZG1faW50ZWdyYXRpb24hYjE1NC5zZG11c2VyIiwic2RtLWRpLURPQ1VNRU5UTUFOQUdFTUVOVC1zZG1faW50ZWdyYXRpb24hYjE1NC5zZG1hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1ET0NVTUVOVE1BTkFHRU1FTlQtc2RtX2ludGVncmF0aW9uIWIxNTQuc2RtYnVzaW5lc3NhZG1pbiJdLCJzY29wZSI6WyJzZG0tZGktRE9DVU1FTlRNQU5BR0VNRU5ULXNkbV9pbnRlZ3JhdGlvbiFiMTU0LnNkbXVzZXIiLCJ1YWEucmVzb3VyY2UiLCJzZG0tZGktRE9DVU1FTlRNQU5BR0VNRU5ULXNkbV9pbnRlZ3JhdGlvbiFiMTU0LnNkbW1pZ3JhdGlvbmFkbWluIiwic2RtLWRpLURPQ1VNRU5UTUFOQUdFTUVOVC1zZG1faW50ZWdyYXRpb24hYjE1NC5zZG1hZG1pbiIsInNkbS1kaS1ET0NVTUVOVE1BTkFHRU1FTlQtc2RtX2ludGVncmF0aW9uIWIxNTQuc2RtYnVzaW5lc3NhZG1pbiJdLCJjbGllbnRfaWQiOiJzYi01NmUzNzIwNS1iYTk2LTRkMDQtOWM3ZC0zYTE4ZjA4ODE0ZjEhYjI1NTcyfHNkbS1kaS1ET0NVTUVOVE1BTkFHRU1FTlQtc2RtX2ludGVncmF0aW9uIWIxNTQiLCJjaWQiOiJzYi01NmUzNzIwNS1iYTk2LTRkMDQtOWM3ZC0zYTE4ZjA4ODE0ZjEhYjI1NTcyfHNkbS1kaS1ET0NVTUVOVE1BTkFHRU1FTlQtc2RtX2ludGVncmF0aW9uIWIxNTQiLCJhenAiOiJzYi01NmUzNzIwNS1iYTk2LTRkMDQtOWM3ZC0zYTE4ZjA4ODE0ZjEhYjI1NTcyfHNkbS1kaS1ET0NVTUVOVE1BTkFHRU1FTlQtc2RtX2ludGVncmF0aW9uIWIxNTQiLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6ImUxZGZhZDAwIiwiaWF0IjoxNzQ5NzgwNDY0LCJleHAiOjE3NDk4MjM2NjQsImlzcyI6Imh0dHBzOi8vYnRxLWMyM3ppaG9pLmF1dGhlbnRpY2F0aW9uLmFwMjEuaGFuYS5vbmRlbWFuZC5jb20vb2F1dGgvdG9rZW4iLCJ6aWQiOiI1ZGE1OGFlNS05Mzg0LTQzMzQtOWM2Yi1mYTZjYjBlNDFlMDgiLCJhdWQiOlsic2ItNTZlMzcyMDUtYmE5Ni00ZDA0LTljN2QtM2ExOGYwODgxNGYxIWIyNTU3MnxzZG0tZGktRE9DVU1FTlRNQU5BR0VNRU5ULXNkbV9pbnRlZ3JhdGlvbiFiMTU0IiwidWFhIiwic2RtLWRpLURPQ1VNRU5UTUFOQUdFTUVOVC1zZG1faW50ZWdyYXRpb24hYjE1NCJdfQ.ZLhwyd7X6JaWbSMF3jfk65hc4MakyUskJMc_aCwMBmmUtRQkWf1vYE3GY47FUlHEDh-rZllyU9--gJNg_Er_ozRfdOUNlSGxth8PjO-ZV1B-yNJ8wuc7D6uABlU5z9YJO2OsvQICwfkGwsibbAiXbfXqImXybZHOcY-STsmIoa1vh_rBTJKTTzbh1o2BWyJ_UEn-Ht5sMHaQ28h4ffxOazcbvq97LGZ0vc3UqL4inetg9MWYg4sgXvf1qZqOevKuJ-hQoF5HI68FsCDz1D_fVVFrB9gXMkK-L3Ligm_fvohnyamFohGbUuJ5vqklc9R-gxpSWKpcgVFtKm8vk0iAnA";
//
//        try {
//            URL url = new URL(fileURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("Authorization", bearerToken);
//
//            // Get response code
//            int responseCode = conn.getResponseCode();
//            System.out.println("Response Code: " + responseCode);
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                // Get filename from headers
//                String contentDisposition = conn.getHeaderField("Content-Disposition");
//                String fileName = "downloaded_file";
//                if (contentDisposition != null && contentDisposition.contains("filename=")) {
//                    int index = contentDisposition.indexOf("filename=") + 9;
//                    fileName = contentDisposition.substring(index).replace("\"", "");
//                }
//                System.out.println("Filename: " + fileName);
//
//                // Set download path
//                String savePath = "D:/" + fileName;
//
//                // Read and write file
//                try (InputStream inputStream = conn.getInputStream();
//                     FileOutputStream outputStream = new FileOutputStream(savePath)) {
//
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, bytesRead);
//                    }
//
//                    System.out.println("File downloaded to: " + savePath);
//                }
//            } else {
//                System.out.println("Failed to download file. Server responded with HTTP code: " + responseCode);
//            }
//
//            conn.disconnect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
