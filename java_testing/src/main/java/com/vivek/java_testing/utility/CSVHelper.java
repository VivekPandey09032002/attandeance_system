package com.vivek.java_testing.utility;

import com.vivek.java_testing.dto.ResponseDailyReport;
import com.vivek.java_testing.dto.ResponseMonthlyReport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {

    public static ByteArrayInputStream responseDailyReportToCSV(List<ResponseDailyReport> reports) {
        final CSVFormat format = CSVFormat.EXCEL;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            csvPrinter.printRecord(Arrays.asList(
                    "user_id",
                    "user_name",
                    "in_time",
                    "out_time",
                    "is_late",
                    "hours_completed"));
            for (ResponseDailyReport report : reports) {
                List<String> data = Arrays.asList(
                        report.getUserId(),
                        report.getUserName(),
                        report.getInTime().toString(),
                        "null",
                        String.valueOf(report.isLate()).toLowerCase(),
                        String.valueOf(report.getHourCompletion()));
                if (report.getOutTime() != null)
                    data.set(3, report.getOutTime().toString());
                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream responseMonthlyReportToCSV(List<ResponseMonthlyReport> reports) {
        final CSVFormat format = CSVFormat.EXCEL;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            csvPrinter.printRecord(Arrays.asList(
                    "user_id",
                    "user_name",
                    "total_attendance",
                    "user_leaves",
                    "no_of_late_days",
                    "hours_completed"));
            for (ResponseMonthlyReport report : reports) {
                List<String> data = Arrays.asList(
                        report.getUserId(),
                        report.getUserName(),
                        String.valueOf(report.getTotalAttendance()),
                        String.valueOf(report.getLeaves()),
                        String.valueOf(report.getLateDays()),
                        String.valueOf(report.getHourCompletion()));

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}