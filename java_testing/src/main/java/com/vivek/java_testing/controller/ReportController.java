package com.vivek.java_testing.controller;

import static com.vivek.java_testing.utility.CSVHelper.responseDailyReportToCSV;
import static com.vivek.java_testing.utility.CSVHelper.responseMonthlyReportToCSV;
import static com.vivek.java_testing.utility.ReportUtility.getResponseMonthlyReport;
import static com.vivek.java_testing.utility.ReportUtility.hourCompleted;
import static com.vivek.java_testing.utility.ReportUtility.isLate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivek.java_testing.dto.ResponseBody;
import com.vivek.java_testing.dto.ResponseDailyReport;
import com.vivek.java_testing.dto.ResponseMonthlyReport;
import com.vivek.java_testing.entity.User;
import com.vivek.java_testing.repository.AttendanceRepository;
import com.vivek.java_testing.repository.UserRepository;
import com.vivek.java_testing.utility.ReportUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Log4j2
@PropertySource("classpath:user.properties")
public class ReportController {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Value("${login.time}")
    private String loginTime;
    @Value("${daily.csv.filename}")
    private String dailyCsvFileName;
    @Value("${monthly.csv.filename}")
    private String monthlyCsvFileName;

    @GetMapping("/download/daily")
    public ResponseEntity<Resource> downloadDailyReport() {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setSkipNullEnabled(true);
        final var userReports = attendanceRepository.findByAttendanceDate(LocalDate.now());
        log.debug("number of attendance found: {}", userReports.size());
        final var responseDto = userReports.stream()
                .map((userReport) -> modelMapper.map(userReport, ResponseDailyReport.class)).toList();
        for (ResponseDailyReport report : responseDto) {
            LocalTime currentTime = LocalTime.parse(loginTime, DateTimeFormatter.ISO_LOCAL_TIME);
            LocalTime inTime = report.getInTime();
            LocalTime outTime = report.getOutTime();
            report.setLate(isLate(inTime, currentTime));
            if (outTime != null)
                report.setHourCompletion(hourCompleted(inTime, outTime));
            else
                report.setHourCompletion(0);

        }
        log.info("daily report response generated");
        InputStreamResource file = new InputStreamResource(responseDailyReportToCSV(responseDto));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dailyCsvFileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/download/monthly")
    public ResponseEntity<Resource> downloadMonthlyReport() {
        LocalTime currentTime = LocalTime.parse(loginTime, DateTimeFormatter.ISO_LOCAL_TIME);
        LocalDate firstMonthDate = LocalDate.now().withDayOfMonth(1);
        List<User> users = userRepository.findAll();
        log.debug("number of users found : {}", users.size());
        List<ResponseMonthlyReport> reportList = new ArrayList<>();
        for (User user : users) {
            final var userMonthlyAttendance = attendanceRepository.getUserMonthlyAttendance(user.getUserId(),
                    firstMonthDate);
            if (userMonthlyAttendance.isEmpty()) {
                final var responseTemp = ResponseMonthlyReport.builder()
                        .userName(user.getUserName())
                        .userId(user.getUserId())
                        .totalAttendance(ReportUtility.addDaysSkippingWeekends(firstMonthDate, LocalDate.now()))
                        .leaves(0)
                        .lateDays(0)
                        .hourCompletion(0)
                        .build();
                reportList.add(responseTemp);
                continue;
            }
            final var responseDto = getResponseMonthlyReport(userMonthlyAttendance, currentTime);
            reportList.add(responseDto);
        }
        log.info("monthly response generated ");
        InputStreamResource file = new InputStreamResource(responseMonthlyReportToCSV(reportList));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + monthlyCsvFileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/daily")
    public ResponseEntity<ResponseBody<Object>> generateDailyReport() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE)
                .setSkipNullEnabled(true);
        final var userReports = attendanceRepository.findByAttendanceDate(LocalDate.now());
        log.debug("number of attendance found: {}", userReports.size());
        final var responseDto = userReports.stream()
                .map((userReport) -> modelMapper.map(userReport, ResponseDailyReport.class)).toList();
        for (ResponseDailyReport report : responseDto) {
            LocalTime currentTime = LocalTime.parse(loginTime, DateTimeFormatter.ISO_LOCAL_TIME);
            LocalTime inTime = report.getInTime();
            LocalTime outTime = report.getOutTime();
            report.setLate(isLate(inTime, currentTime));
            if (outTime != null)
                report.setHourCompletion(hourCompleted(inTime, outTime));
            else
                report.setHourCompletion(0);
        }
        final var responseBody = ResponseBody.builder().data(Collections.singletonList(responseDto))
                .message("user saved successfully").status(HttpStatus.CREATED).build();
        log.info("daily report generated");
        return ResponseEntity.ok(responseBody);

    }

    @GetMapping("/monthly")
    public ResponseEntity<ResponseBody<Object>> generateMonthlyReport() {
        LocalTime currentTime = LocalTime.parse(loginTime, DateTimeFormatter.ISO_LOCAL_TIME);
        LocalDate firstMonthDate = LocalDate.now().withDayOfMonth(1);
        List<User> users = userRepository.findAll();
        log.debug("number of users found : {}", users.size());
        List<ResponseMonthlyReport> reportList = new ArrayList<>();
        for (User user : users) {
            final var userMonthlyAttendance = attendanceRepository.getUserMonthlyAttendance(user.getUserId(),
                    firstMonthDate);
            if (userMonthlyAttendance.isEmpty()) {
                final var responseTemp = ResponseMonthlyReport.builder()
                        .userName(user.getUserName())
                        .userId(user.getUserId())
                        .totalAttendance(ReportUtility.addDaysSkippingWeekends(firstMonthDate, LocalDate.now()))
                        .leaves(0)
                        .lateDays(0)
                        .hourCompletion(0)
                        .build();
                reportList.add(responseTemp);
                continue;
            }

            final var responseDto = getResponseMonthlyReport(userMonthlyAttendance, currentTime);
            reportList.add(responseDto);
        }
        final var responseBody = ResponseBody.builder().data(Collections.singletonList(reportList))
                .message("user saved successfully").status(HttpStatus.CREATED).build();
        log.info("monthly response generated ");
        return ResponseEntity.ok(responseBody);

    }
}
