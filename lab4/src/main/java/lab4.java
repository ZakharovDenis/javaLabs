import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;


public class lab4 {

    private static Map<ArrayList<ArrayList<LocalTime>>, Map<String, Map<String, Object>>> timeManager = new HashMap<ArrayList<ArrayList<LocalTime>>, Map<String, Map<String, Object>>>();

    private static Map<String, Map<String, ArrayList<ArrayList<Object>>>> categories = new HashMap<String, Map<String, ArrayList<ArrayList<Object>>>>() {{
        put("Work", new HashMap<String, ArrayList<ArrayList<Object>>>() {{
            put("Intellij", new ArrayList<ArrayList<Object>>());
        }});
        put("Browsing", new HashMap<String, ArrayList<ArrayList<Object>>>() {{
            put("Chrome", new ArrayList<ArrayList<Object>>());
            put("Firefox", new ArrayList<ArrayList<Object>>());
        }});
        put("Messaging", new HashMap<String, ArrayList<ArrayList<Object>>>() {{
            put("Telegram", new ArrayList<ArrayList<Object>>());
            put("Thunderbird", new ArrayList<ArrayList<Object>>());
        }});
    }};


    private static Map<String, ArrayList<String>> categories2 = new HashMap<String, ArrayList<String>>() {{
        put("Work", new ArrayList<String>() {{
            add("Intellij");
        }});
        put("Browsing", new ArrayList<String>() {{
            add("Chrome");
            add("Firefox");
        }});
        put("Messaging", new ArrayList<String>() {{
            add("Telegram");
            add("Thunderbird");
        }});
    }};

    private static String getUserName() throws IOException, InterruptedException {
        String sname;
        String finalname = "Invalid";
        Process name = Runtime.getRuntime().exec("whoami");
        BufferedReader brname = new BufferedReader(
                new InputStreamReader(name.getInputStream()));
        while ((sname = brname.readLine()) != null) {
            finalname = sname;
        }
        name.waitFor();
        name.destroy();
        return finalname;
    }

    private static Map<ArrayList<LocalTime>, Map<String, Map<String, Long>>> countGap(ArrayList<ArrayList<LocalTime>> gaps, Map<String, Map<String, ArrayList<ArrayList<Object>>>> time) {
        Map<ArrayList<LocalTime>, Map<String, Map<String, Long>>> comparison = new HashMap<>();
        for (String workType : time.keySet()) {
            for (String app : time.get(workType).keySet()) {
                if (time.get(workType).get(app).size() != 0) {
                    ArrayList<Object> appInfo = time.get(workType).get(app).get(0);
                    LocalTime startTime = (LocalTime) appInfo.get(0);
                    LocalTime finishTime = (LocalTime) appInfo.get(1);
                    for (int i = 0; i < gaps.size(); i++) {
                        ArrayList<LocalTime> currentGap = gaps.get(i);
                        if (currentGap.get(0).isBefore(startTime) && (currentGap.get(1).isAfter(startTime) || currentGap.get(1).getHour() == 0)) {
                            Map<String, Long> appMap = new HashMap<String, Long>();
                            if (currentGap.get(1).isAfter(finishTime) || currentGap.get(1).getHour() == 0) {
                                appMap.put(app, startTime.until(finishTime, MINUTES));
                            } else {
                                appMap.put(app, startTime.until(currentGap.get(1), MINUTES));
                            }
                            Map<String, Map<String, Long>> workMap = new HashMap<String, Map<String, Long>>(){{
                                put(workType, appMap);
                            }};
                            if (comparison.get(currentGap) != null){
                                workMap.putAll(comparison.get(currentGap));
                            }
                            comparison.put(currentGap, workMap);
                            for (int j = i; j < gaps.size(); j++) {
                                ArrayList<LocalTime> secondCurrentGap = gaps.get(j);
                                if (secondCurrentGap.get(0).compareTo(finishTime) == -1 && secondCurrentGap.get(1).compareTo(finishTime) == 1) {
                                    if (currentGap != secondCurrentGap) {
                                        Long hours = currentGap.get(1).until(secondCurrentGap.get(0), HOURS);
                                        for (int k = 0; k < hours; k++) {
                                            ArrayList<LocalTime> gapToAdd = gaps.get(i + k + 1);
                                            comparison.put(gapToAdd, new HashMap<String, Map<String, Long>>() {{
                                                put(workType, new HashMap<String, Long>() {{
                                                    put(app, 60L);
                                                }});
                                            }});
                                        }
                                        comparison.put(secondCurrentGap, new HashMap<String, Map<String, Long>>() {{
                                            put(workType, new HashMap<String, Long>() {{
                                                put(app, secondCurrentGap.get(0).until(finishTime, MINUTES));
                                            }});
                                        }});
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return comparison;
    }

    private static long getMinuteDuration(LocalTime t) {
        long h = t.getHour();
        long m = t.getMinute();
        return (h * 60) + m;
    }

    public static void main(String[] args) throws IOException {
        final ArrayList<LocalTime> timestaps = new ArrayList<LocalTime>();
        for (int i = 0; i < 25; i++) {
            if (i < 10) {
                timestaps.add(LocalTime.parse("0" + i + ":00", DateTimeFormatter.ofPattern("HH:mm")));
            } else {
                timestaps.add(LocalTime.parse(i + ":00", DateTimeFormatter.ofPattern("HH:mm")));
            }
        }
        ArrayList<ArrayList<LocalTime>> timeGaps = new ArrayList<ArrayList<LocalTime>>();
        for (int i = 0; i < timestaps.size() - 1; i++) {
            final int finalI = i;
            timeGaps.add(new ArrayList<LocalTime>() {{
                add(timestaps.get(finalI));
                add(timestaps.get(finalI + 1));
            }});
        }
        String s;
        Process p;
        try {
            String name = getUserName();
            p = Runtime.getRuntime().exec("ps -aux");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                if (s.contains(name)) {
                    for (final String workType : categories.keySet()) {
                        for (final String app : categories.get(workType).keySet()) {
                            if (s.contains(app) || s.contains(app.toLowerCase())) {
                                String[] mass = s.split(" ");
                                ArrayList<String> filtered = new ArrayList<String>();
                                for (String word : mass) {
                                    if (!word.equals("")) {
                                        filtered.add(word);
                                    }
                                }
                                try {
                                    LocalTime starttime;
                                    LocalTime worktime;
                                    try {
                                        starttime = LocalTime.parse(filtered.get(8), DateTimeFormatter.ofPattern("HH:mm"));
                                    } catch (Exception e) {
                                        starttime = LocalTime.parse(filtered.get(8), DateTimeFormatter.ofPattern("H:mm"));
                                    }
                                    String tmp = filtered.get(9);
                                    if (tmp.length() == 5) {
                                        tmp = "00:" + tmp;
                                    } else if (tmp.length() == 4) {
                                        tmp = "00:0" + tmp;
                                    }
                                    try {
                                        worktime = LocalTime.parse(tmp, DateTimeFormatter.ofPattern("HH:mm:ss"));
                                    } catch (Exception e) {
                                        worktime = LocalTime.parse(tmp, DateTimeFormatter.ofPattern("H:mm:ss"));
                                    }
                                    final String processStaus = filtered.get(7);
                                    final LocalTime finalStarttime = starttime;
                                    final LocalTime finalWorktime = worktime;
                                    ArrayList<Object> output = new ArrayList<Object>() {{
                                        add(finalStarttime);
                                        add(finalWorktime);
                                    }};
                                    Map<String, ArrayList<ArrayList<Object>>> innerMap = categories.get(workType);
                                    ArrayList<ArrayList<Object>> innerArraay = innerMap.get(app);
                                    innerArraay.add(output);
                                    innerMap.put(app, innerArraay);
                                    categories.put(workType, innerMap);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                        }
                    }
                }
            p.waitFor();
            for (String workType : categories.keySet()) {
                for (final String app : categories.get(workType).keySet()) {
                    if (categories.get(workType).get(app).size() > 1) {
                        LocalTime mintime = (LocalTime) categories.get(workType).get(app).get(0).get(0);
                        LocalTime maxtime = (LocalTime) categories.get(workType).get(app).get(0).get(0);
                        for (ArrayList<Object> info : categories.get(workType).get(app)) {
                            if (((LocalTime) info.get(0)).isBefore(mintime)) {
                                mintime = (LocalTime) info.get(0);
                            } else if (((LocalTime) info.get(0)).isAfter(maxtime)) {
                                maxtime = (LocalTime) info.get(0);
                            }
                        }

                        final LocalTime finalmintime = mintime;
                        final LocalTime finalmaxtime = maxtime;
                        final ArrayList<Object> output = new ArrayList<Object>() {{
                            add(finalmintime);
                            add(finalmaxtime);
                        }};
                        ArrayList<ArrayList<Object>> toPut = new ArrayList<ArrayList<Object>>() {{
                            add(output);
                        }};
                        Map<String, ArrayList<ArrayList<Object>>> innerMap = categories.get(workType);
                        innerMap.put(app, toPut);
                        categories.put(workType, innerMap);
                    } else if (categories.get(workType).get(app).size() == 1) {
                        final LocalTime mintime = (LocalTime) categories.get(workType).get(app).get(0).get(0);
                        final LocalTime maxtime = (LocalTime) categories.get(workType).get(app).get(0).get(1);
                        final ArrayList<Object> output = new ArrayList<Object>() {{
                            add(mintime);
                            add(mintime.plus(getMinuteDuration(maxtime), MINUTES));
                        }};
                        ArrayList<ArrayList<Object>> toPut = new ArrayList<ArrayList<Object>>() {{
                            add(output);
                        }};
                        Map<String, ArrayList<ArrayList<Object>>> innerMap = categories.get(workType);
                        innerMap.put(app, toPut);
                        categories.put(workType, innerMap);
                    }
                }
            }
            System.out.println(countGap(timeGaps, categories));
            p.destroy();

        } catch (Exception e) {
        }
    }
}
