package com.vivek.java_testing.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vivek.java_testing.dto.RequestUser;
import com.vivek.java_testing.dto.ResponseBody;
import com.vivek.java_testing.entity.Attendance;
import com.vivek.java_testing.entity.User;
import com.vivek.java_testing.exception.CustomException;
import com.vivek.java_testing.repository.AttendanceRepository;
import com.vivek.java_testing.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Log4j2
@RequiredArgsConstructor
public class UserQrCodeController {
        private final ModelMapper mapper;

        private final UserRepository userRepository;
        private static final String USER_IN_TIME_ERROR = "Cannot register in time ";
        private static final String USER_OUT_TIME_ERROR = "Cannot register out time";
        private static final String USER_ERROR = "Error perfoming operation in user";
        private final AttendanceRepository attendanceRepository;

        @PostMapping("/signInCode")
        public ResponseEntity<ResponseBody<Object>> imageData(@RequestBody RequestUser requestUser) {
                User user = userRepository.findById(requestUser.getUserId())
                                .orElseThrow(() -> new CustomException(USER_ERROR,
                                                List.of("No user found with given id: " + requestUser.getUserId()),
                                                HttpStatus.NOT_FOUND));
                if (!Objects.equals(user.getKey(), requestUser.getKey())) {
                        final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                        .message("Details Not Matched")
                                        .status(HttpStatus.BAD_REQUEST).build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

                } else if (!Objects.equals(user.getPassword(), requestUser.getPassword())) {
                        final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                        .message("Password is Incorrect")
                                        .status(HttpStatus.BAD_REQUEST).build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
                }

                if (attendanceRepository.getCurrentDayAttendance(user.getUserId(), LocalDate.now()).isPresent()) {
                        throw new CustomException(USER_IN_TIME_ERROR,
                                        List.of("Already entered in time for current day"),
                                        HttpStatus.BAD_REQUEST);
                }
                // saving the user info the database
                Attendance currentDayAttendance = Attendance.builder()
                                .attendanceDate(LocalDate.now())
                                .inTime(LocalTime.now())
                                .user(user)
                                .build();
                attendanceRepository.save(currentDayAttendance);
                log.info("In time attendance saved: ");
                final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                .message(requestUser.getUserName() + " (" + requestUser.getUserId() + ")  signed in at "
                                                + LocalTime.now()
                                                                .format(DateTimeFormatter
                                                                                .ofLocalizedTime(FormatStyle.SHORT)
                                                                                .withLocale(Locale.US)))
                                .status(HttpStatus.OK).build();

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }

        @PostMapping("/signOutCode")
        public ResponseEntity<ResponseBody<Object>> signOutCode(@RequestBody RequestUser requestUser) {

                User user = userRepository.findById(requestUser.getUserId())
                                .orElseThrow(() -> new CustomException(USER_ERROR,
                                                List.of("No user found with id: " + requestUser.getUserId()),
                                                HttpStatus.NOT_FOUND));
                if (!Objects.equals(user.getKey(), requestUser.getKey())) {
                        final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                        .message("Details Not Matched")
                                        .status(HttpStatus.BAD_REQUEST).build();

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

                } else if (!Objects.equals(user.getPassword(), requestUser.getPassword())) {
                        final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                        .message("Password is Incorrect")
                                        .status(HttpStatus.BAD_REQUEST).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
                }
                Attendance attendance = attendanceRepository
                                .getCurrentDayAttendance(requestUser.getUserId(), LocalDate.now())
                                .orElseThrow(() -> new CustomException(USER_OUT_TIME_ERROR,
                                                List.of("No in time found for the user: " + user.getUserId()),
                                                HttpStatus.BAD_REQUEST));
                if (attendance.getOutTime() != null) {
                        throw new CustomException("Cannot register out-time",
                                        List.of("Already signed out for today at " + attendance.getOutTime()
                                                        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                                                                        .withLocale(Locale.US))),
                                        HttpStatus.BAD_REQUEST);
                }
                attendance.setOutTime(LocalTime.now());
                attendance = attendanceRepository.save(attendance);
                log.info("Out time attendance saved");
                final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                .message(requestUser.getUserName() + " (" + requestUser.getUserId() + ") signed out at "
                                                + LocalTime.now()
                                                                .format(DateTimeFormatter
                                                                                .ofLocalizedTime(FormatStyle.SHORT)
                                                                                .withLocale(Locale.US)))
                                .status(HttpStatus.OK).build();

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);

        }

        @PostMapping("/register-user/code")
        public ResponseEntity<ResponseBody<Object>> registerCode(@RequestBody RequestUser requestUser) {

                Optional<User> userOptional = userRepository.findById(requestUser.getUserId());
                if (userOptional.isEmpty()) {
                        User user = mapper.map(requestUser, User.class);
                        userRepository.save(user);

                        final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                        .message("User register successfully")
                                        .status(HttpStatus.CREATED).build();

                        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
                }
                User user = userOptional.get();
                if (requestUser.getKey().equals(user.getKey())) {
                        final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                        .message("User already registred")
                                        .status(HttpStatus.FOUND).build();
                        return ResponseEntity.status(HttpStatus.FOUND).body(responseBody);

                }
                mapper.map(requestUser, user);
                userRepository.save(user);

                final var responseBody = ResponseBody.builder().data(List.of(requestUser))
                                .message("User updated successfully")
                                .status(HttpStatus.OK).build();

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);

        }
}
