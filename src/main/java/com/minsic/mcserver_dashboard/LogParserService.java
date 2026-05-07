package com.minsic.mcserver_dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.GZIPInputStream;

@Service
public class LogParserService {

    @Value("${minecraft.log.path}")
    private String logPath;

    private static final Pattern JOIN_PATTERN = Pattern.compile(
            "\\[(\\d{2}:\\d{2}:\\d{2})\\] \\[Server thread/INFO\\]: ([\\w]+) joined the game"
    );
    private static final Pattern LEAVE_PATTERN = Pattern.compile(
            "\\[(\\d{2}:\\d{2}:\\d{2})\\] \\[Server thread/INFO\\]: ([\\w]+) left the game"
    );
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<PlayerStats> parsePlayerStats() {
        Map<String, List<Long>> sessionMap = new LinkedHashMap<>();
        Map<String, LocalTime> joinTimes = new HashMap<>();
        Map<String, String> lastSeenMap = new HashMap<>();

        try {
            File logsDir = new File(logPath);
            File[] allFiles = logsDir.listFiles();
            if (allFiles == null) return new ArrayList<>();

            List<File> logFiles = new ArrayList<>();
            for (File f : allFiles) {
                if (f.getName().endsWith(".log.gz") || f.getName().equals("latest.log")) {
                    logFiles.add(f);
                }
            }
            logFiles.sort(Comparator.comparing(File::getName));

            // latest.log를 맨 마지막으로
            File latest = new File(logPath + "/latest.log");
            logFiles.remove(latest);
            logFiles.add(latest);

            for (File file : logFiles) {
                String dateStr = file.getName().equals("latest.log")
                        ? LocalDate.now().toString()
                        : file.getName().substring(0, 10);

                BufferedReader reader;
                if (file.getName().endsWith(".gz")) {
                    reader = new BufferedReader(new InputStreamReader(
                            new GZIPInputStream(new FileInputStream(file)), "UTF-8"
                    ));
                } else {
                    reader = new BufferedReader(new InputStreamReader(
                            new FileInputStream(file), "UTF-8"
                    ));
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher joinMatcher = JOIN_PATTERN.matcher(line);
                    Matcher leaveMatcher = LEAVE_PATTERN.matcher(line);

                    if (joinMatcher.find()) {
                        String name = joinMatcher.group(2);
                        LocalTime time = LocalTime.parse(joinMatcher.group(1), TIME_FMT);
                        joinTimes.put(name, time);
                        lastSeenMap.put(name, dateStr);
                        sessionMap.putIfAbsent(name, new ArrayList<>());
                    } else if (leaveMatcher.find()) {
                        String name = leaveMatcher.group(2);
                        LocalTime leaveTime = LocalTime.parse(leaveMatcher.group(1), TIME_FMT);
                        if (joinTimes.containsKey(name)) {
                            long secs = Duration.between(joinTimes.get(name), leaveTime).getSeconds();
                            if (secs < 0) secs += 86400;
                            sessionMap.get(name).add(secs);
                            joinTimes.remove(name);
                        }
                    }
                }
                reader.close();
            }

            // 아직 접속 중인 플레이어 (leave 없는 경우)
            for (Map.Entry<String, LocalTime> entry : joinTimes.entrySet()) {
                String name = entry.getKey();
                long secs = Duration.between(entry.getValue(), LocalTime.now()).getSeconds();
                if (secs < 0) secs = 0;
                sessionMap.computeIfAbsent(name, k -> new ArrayList<>()).add(secs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PlayerStats> result = new ArrayList<>();
        for (Map.Entry<String, List<Long>> entry : sessionMap.entrySet()) {
            String name = entry.getKey();
            List<Long> sessions = entry.getValue();
            long totalSecs = sessions.stream().mapToLong(Long::longValue).sum();
            long hours = totalSecs / 3600;
            long mins = (totalSecs % 3600) / 60;
            String playTime = hours + "시간 " + mins + "분";
            String lastSeen = lastSeenMap.getOrDefault(name, "알 수 없음");
            result.add(new PlayerStats(name, lastSeen, playTime, sessions.size()));
        }
        result.sort((a, b) -> b.getLoginCount() - a.getLoginCount());
        return result;
    }
}