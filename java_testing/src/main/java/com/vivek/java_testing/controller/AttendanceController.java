package com.vivek.java_testing.controller;

import com.vivek.java_testing.dto.RequestImage;
import com.vivek.java_testing.dto.ResponseAttendanceDetail;
import com.vivek.java_testing.dto.ResponseBody;
import com.vivek.java_testing.entity.Attendance;
import com.vivek.java_testing.entity.User;
import com.vivek.java_testing.exception.CustomException;
import com.vivek.java_testing.repository.AttendanceRepository;
import com.vivek.java_testing.repository.UserRepository;
import com.vivek.java_testing.service.UploadService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
@Log4j2
public class AttendanceController {
    private final UserRepository userRepository;
    private final UploadService uploadService;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper mapper;
    private static final String USER_IN_TIME_ERROR = "Cannot register in time ";
    private static final String USER_OUT_TIME_ERROR = "Cannot register out time";

    @PostMapping("/in-time")
    public ResponseEntity<ResponseBody<Object>> registerUserInTime(@RequestBody RequestImage requestImage,
            HttpServletRequest request)
            throws IOException {
        final var userId = requestImage.getUserId();
        log.debug("User userId {}", userId);
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_IN_TIME_ERROR,
                        List.of("No user found with userId : " + userId), HttpStatus.NOT_FOUND));
        // image data
        String data = requestImage.getData();
        String[] strings = data.split(",");
        // getting file extension or png default
        String extension = getExtension(strings);
        log.debug("file extension: {}", extension);
        // attendance already found
        if (attendanceRepository.getCurrentDayAttendance(user.getUserId(), LocalDate.now()).isPresent()) {
            throw new CustomException(USER_IN_TIME_ERROR, List.of("Already signed in for the day at " + LocalTime.now()
                    .format(DateTimeFormatter
                            .ofLocalizedTime(FormatStyle.SHORT)
                            .withLocale(Locale.US))),
                    HttpStatus.BAD_REQUEST);
        }
        // saving the user info the database
        Attendance currentDayAttendance = Attendance.builder()
                .attendanceDate(LocalDate.now())
                .inTime(LocalTime.now())
                .user(user)
                .build();
        attendanceRepository.save(currentDayAttendance);
        log.info("in time attendance saved");
        // convert base64 string to binary data and saving to classpath
        uploadService.saveImage(fileName(userId, extension), strings[1]);
        log.info("image saved ");
        final var responseDto = mapper.map(currentDayAttendance, ResponseAttendanceDetail.class);
        final var responseBody = ResponseBody.builder().data(List.of(responseDto)).message("In time attendance saved")
                .status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);

    }

    @GetMapping("/out-time/{userId}")
    public ResponseEntity<ResponseBody<Object>> registerUserOutTime(@PathVariable String userId) throws IOException {
        log.debug("user userId {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_OUT_TIME_ERROR,
                        List.of("No user found with the given id: " + userId),
                        HttpStatus.BAD_REQUEST));

        Attendance attendance = attendanceRepository.getCurrentDayAttendance(userId, LocalDate.now())
                .orElseThrow(() -> new CustomException(USER_OUT_TIME_ERROR,
                        List.of("No in time found for the userId: " + userId),
                        HttpStatus.BAD_REQUEST));
        if (attendance.getOutTime() != null) {
            throw new CustomException(
                    "Cannot register out-time", List.of("Already signed out for the day " + LocalTime.now()
                            .format(DateTimeFormatter
                                    .ofLocalizedTime(FormatStyle.SHORT)
                                    .withLocale(Locale.US))),
                    HttpStatus.BAD_REQUEST);
        }
        attendance.setOutTime(LocalTime.now());
        attendance = attendanceRepository.save(attendance);
        log.info("out time attendance saved");
        final var responseDto = mapper.map(attendance, ResponseAttendanceDetail.class);
        final var responseBody = ResponseBody.builder().data(List.of(responseDto)).message("Out time attendance saved")
                .status(HttpStatus.CREATED).build();
        return ResponseEntity.ok(responseBody);

    }

    private static String fileName(String name, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return name + currentTimeStamp + "." + extension;
    }

    private static String getExtension(String[] strings) {
        String extension;
        switch (strings[0]) {// check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default:// should write cases for more images types
                extension = "jpg";
                break;
        }
        return extension;
    }
}
