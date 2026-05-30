package org.han.webtest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.ClassBookingRepository;
import org.han.webtest.repository.ClassScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BookingController {

    private final ClassBookingRepository classBookingRepository;
    private final ClassScheduleRepository classScheduleRepository;

    public BookingController(ClassBookingRepository classBookingRepository, ClassScheduleRepository classScheduleRepository) {
        this.classBookingRepository = classBookingRepository;
        this.classScheduleRepository = classScheduleRepository;
    }

    @PostMapping("/api/bookings/{classId}")
    public ResponseEntity<?> bookClass(@PathVariable Long classId, HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<ClassScheduleModel> scheduleOpt = classScheduleRepository.findById(classId);
        if (scheduleOpt.isPresent()) {
            ClassScheduleModel schedule = scheduleOpt.get();
            if (classBookingRepository.existsByUserAndScheduleAndStatusNot(user, schedule, "REJECTED")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Kamu sudah membooking kelas ini dan statusnya masih aktif!"));
            }

            ClassBookingModel booking = new ClassBookingModel();
            booking.setUser(user);
            booking.setSchedule(schedule);
            booking.setBookingDate(LocalDate.now());
            booking.setStatus("PENDING");
            classBookingRepository.save(booking);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Kelas tidak ditemukan");
    }

    @GetMapping("/user/dashboard")
    public ResponseEntity<?> getCustomerDashboard(HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<ClassBookingModel> bookings = classBookingRepository.findAll().stream()
                .filter(b -> b.getUser().getId() == user.getId())
                .filter(b -> !b.getStatus().equals("REJECTED"))
                .collect(Collectors.toList());

        List<Map<String, Object>> bookedClasses = bookings.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("serviceName", b.getSchedule().getServiceName());
            map.put("dayOfWeek", b.getSchedule().getDayOfWeek());
            map.put("startTime", b.getSchedule().getStartTime().toString());
            map.put("endTime", b.getSchedule().getEndTime().toString());
            map.put("status", b.getStatus());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("bookedClasses", bookedClasses);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id, HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");

        Optional<ClassBookingModel> booking = classBookingRepository.findById(id);

        if (booking.isPresent() && booking.get().getUser().getId() == user.getId()) {
            classBookingRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Booking berhasil dibatalkan"));
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Gagal membatalkan booking, data tidak ditemukan atau bukan milikmu"));
    }

    @GetMapping("/api/trainer/bookings")
    public ResponseEntity<?> getTrainerBookings(HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }


        List<Map<String, Object>> members = classBookingRepository.findAll().stream()
                .filter(b -> b.getSchedule().getTrainerName().equals(user.getUsername()))

                .filter(b -> b.getStatus().equals("APPROVED"))
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("classId", b.getSchedule().getId());
                    map.put("memberName", b.getUser().getUsername());
                    map.put("status", b.getStatus());
                    return map;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(members);
    }
}
