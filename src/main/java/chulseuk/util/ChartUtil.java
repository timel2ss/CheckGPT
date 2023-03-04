package chulseuk.util;

import chulseuk.domain.Log;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


public class ChartUtil {

    private static final int DAYS_OF_CHART = 281;
    private static final int WEEKS_OF_CHART = 40;
    private static final int DAYS_OF_WEEK = 7;

    public static String drawChart(List<Log> logs) {
        LocalDate today = LocalDate.now();

        boolean[] attendances = makeLogsToArray(logs, today);
        int attendanceIdx = DAYS_OF_CHART;


        String[][] chart = new String[8][WEEKS_OF_CHART + 1];
        updateDayOfWeekTitle(chart);

        int DayOfTheWeekIdx = today.getDayOfWeek().getValue();
        LocalDate firstDayOfThisMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
        long betweenFirstDayAndToday = Duration.between(firstDayOfThisMonth.atStartOfDay(), today.atStartOfDay()).toDays();

        int monthIdx = WEEKS_OF_CHART;
        for (int colIdx = WEEKS_OF_CHART; colIdx > 0; colIdx--) {
            for (int rowIdx = DAYS_OF_WEEK; rowIdx > 0; rowIdx--) {
                if (colIdx == WEEKS_OF_CHART && rowIdx > DayOfTheWeekIdx) {
                    chart[rowIdx][colIdx] = " ";
                    continue;
                }

                chart[rowIdx][colIdx] = attendances[attendanceIdx--] ? "■" : "□";

                if (isFirstDayOfThisMonth(attendanceIdx, betweenFirstDayAndToday)) {
                    monthIdx = colIdx;
                }
            }
        }

        updateMonthTitle(chart, firstDayOfThisMonth, monthIdx);
        return makeChartToString(chart);
    }

    @NotNull
    private static boolean[] makeLogsToArray(List<Log> logs, LocalDate today) {
        boolean[] attendances = new boolean[DAYS_OF_CHART + 1];
        logs.stream()
                .map(log -> log.getAttendanceTime().toLocalDate())
                .filter(date -> date.isAfter(today.minusDays(DAYS_OF_CHART)))
                .forEach(date -> {
                    int idx = DAYS_OF_CHART - (int) Duration.between(date.atStartOfDay(), today.atStartOfDay()).toDays();
                    attendances[idx] = true;
                });
        return attendances;
    }

    private static void updateDayOfWeekTitle(String[][] chart) {
        chart[0][0] = "   ";
        chart[1][0] = "Mon";
        chart[2][0] = "   ";
        chart[3][0] = "Wed";
        chart[4][0] = "   ";
        chart[5][0] = "Fri";
        chart[6][0] = "   ";
        chart[7][0] = "Sun";
    }

    private static boolean isFirstDayOfThisMonth(int attendanceIdx, long betweenFirstDayAndToday) {
        return attendanceIdx == DAYS_OF_CHART - betweenFirstDayAndToday;
    }

    private static void updateMonthTitle(String[][] chart, LocalDate firstDayOfThisMonth, int monthIdx) {
        for (int i = 0; i < monthIdx; i += 4) {
            chart[0][monthIdx - i] = String.valueOf(firstDayOfThisMonth.minusWeeks(i).getMonth().getValue());
        }
    }

    @NotNull
    private static String makeChartToString(String[][] chart) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DAYS_OF_WEEK + 1; i++) {
            for (int j = 0; j < WEEKS_OF_CHART + 1; j++) {
                if (chart[i][j] == null) {
                    chart[i][j] = " ";
                }
                if (List.of("10", "11", "12").contains(chart[i][j])) {
                    sb.append(String.format("%s", chart[i][j]));
                    continue;
                }
                sb.append(String.format("%s ", chart[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
