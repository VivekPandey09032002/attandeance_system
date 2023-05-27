package com.vivek.java_testing.repository;

import com.vivek.java_testing.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance,Integer> {
    @Query("select a from Attendance a where a.user.userId = ?1 and a.attendanceDate = ?2")
    Optional<Attendance> getCurrentDayAttendance(String userId, LocalDate attendanceDate);

    @Query("select a from Attendance a where a.user.userId = ?1")
    List<Attendance> getUserAttendance(String userId);

    @Query("select a from Attendance a where a.attendanceDate = ?1")
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);


    @Query("select a from Attendance a where a.user.userId = ?1 and a.attendanceDate >= ?2")
    List<Attendance> getUserMonthlyAttendance(String userId, LocalDate attendanceDate);

}
