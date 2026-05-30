package org.han.webtest.service;

import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.ClassBookingRepository;
import org.han.webtest.repository.ClassScheduleRepository;
import org.han.webtest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClassService {

    private final ClassScheduleRepository scheduleRepository;
    private final ClassBookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ClassService(ClassScheduleRepository scheduleRepository, ClassBookingRepository bookingRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public List<ClassScheduleModel> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<ClassScheduleModel> getSchedulesByDay(String dayOfWeek) {
        return scheduleRepository.findByDayOfWeek(dayOfWeek);
    }

    public ClassBookingModel bookClass(Long userId, Long scheduleId, LocalDate bookingDate) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        if (bookingRepository.existsByUserAndScheduleAndStatusNot(user, schedule, "REJECTED")) {
            throw new RuntimeException("Kamu sudah membooking kelas ini dan statusnya masih aktif!");
        }

        ClassBookingModel booking = new ClassBookingModel(user, schedule, bookingDate, "PENDING");
        return bookingRepository.save(booking);
    }

    public List<ClassBookingModel> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }


    public ClassScheduleModel createSchedule(ClassScheduleModel newClass) {
        return scheduleRepository.save(newClass);
    }

    public void deleteSchedule(Long scheduleId) {
        // 1. Cari dulu jadwalnya
        ClassScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<ClassBookingModel> relatedBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getSchedule().getId() == scheduleId)
                .toList();
        bookingRepository.deleteAll(relatedBookings);
        scheduleRepository.delete(schedule);
    }
}