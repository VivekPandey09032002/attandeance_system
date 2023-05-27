package com.vivek.java_testing.controller;

import com.vivek.java_testing.entity.Attendance;
import com.vivek.java_testing.entity.User;
import com.vivek.java_testing.repository.AttendanceRepository;
import com.vivek.java_testing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Log4j2
public class TestController {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    @GetMapping("/record")
    public Attendance testAttendance(){
        User user=  User.builder().userId("2@gmail.com").build();
        userRepository.save(user);
        Attendance attendance =  Attendance.builder().attendanceId(1).attendanceDate(LocalDate.now()).inTime(LocalTime.now()).outTime(LocalTime.now()).user(user).build();
        attendance  =  attendanceRepository.save(attendance);

        log.info("attendance {}",attendance);
        attendanceRepository.save(attendance);
        var attendace =  attendanceRepository.getCurrentDayAttendance(user.getUserId(),LocalDate.now());
        return attendace.get();
    }
}
