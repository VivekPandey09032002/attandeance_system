package com.vivek.java_testing.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.vivek.java_testing.dto.ResponseMonthlyReport;
import com.vivek.java_testing.entity.Attendance;
import com.vivek.java_testing.entity.User;

public class ReportUtility {
    private ReportUtility() {
    }

    public static boolean isLate(LocalTime inTime, LocalTime actualTime) {
        return inTime.compareTo(actualTime) > 0;
    }

    public static double hourCompleted(LocalTime inTime, LocalTime outTime) {
        Duration between = Duration.between(inTime, outTime);
        double result = between.toHoursPart() + ((double) between.toMinutesPart() / 60);
        return BigDecimal.valueOf(result)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static ResponseMonthlyReport getResponseMonthlyReport(List<Attendance> attendances, LocalTime actualTime) {
        long lateDays = attendances.stream().filter(attendance -> isLate(attendance.getInTime(), actualTime)).count();

        double hourCompletion = attendances.stream()
                .map(attendance -> {
                    if (attendance.getOutTime() == null)
                        return 0.0D;
                    return hourCompleted(attendance.getInTime(), attendance.getOutTime());
                })
                .reduce(0.0D, (arg0, arg1) -> Double.sum(arg0, arg1));
        User user = attendances.get(0).getUser();
        LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
        return ResponseMonthlyReport.builder()
                .totalAttendance(addDaysSkippingWeekends(monthStartDate, LocalDate.now()))
                .userId(user.getUserId())
                .leaves(addDaysSkippingWeekends(monthStartDate, LocalDate.now()) - attendances.size())
                .userName(user.getUserName())
                .lateDays((int) lateDays)
                .hourCompletion(hourCompletion)
                .build();

    }

    public static int addDaysSkippingWeekends(LocalDate monthStartDate, LocalDate currentDate) {
        LocalDate result = monthStartDate;
        int addedDays = 0;
        while (result.compareTo(currentDate) <= 0) {
            result = result.plusDays(1);
            if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY || result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                ++addedDays;
            }
        }
        return addedDays;
    }
}
