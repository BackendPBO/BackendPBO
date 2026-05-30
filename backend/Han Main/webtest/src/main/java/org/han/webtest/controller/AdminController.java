package org.han.webtest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.han.webtest.DTO.UserDashboardResponse;
import org.han.webtest.model.UserModel;
import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel; // Import baru
import org.han.webtest.repository.ClassBookingRepository;
import org.han.webtest.service.AdminService;
import org.han.webtest.service.ClassService; // Import baru
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {

    private final AdminService adminService;
    private final ClassBookingRepository classBookingRepository;
    private final ClassService classService;

    public AdminController(AdminService adminService, ClassBookingRepository classBookingRepository, ClassService classService){
        this.adminService = adminService;
        this.classBookingRepository = classBookingRepository;
        this.classService = classService;
    }

    @GetMapping
    public List<UserDashboardResponse> getAllUser(HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");

        if (!user.getRole().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized");
        }

        return adminService.getAllUser();
    }

    @GetMapping("/bookings")
    public List<Map<String, Object>> getAdminBookings(HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");

        if (!user.getRole().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized");
        }

        List<ClassBookingModel> bookings = classBookingRepository.findAll();
        return bookings.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("username", b.getUser().getUsername());
            map.put("serviceName", b.getSchedule().getServiceName());
            map.put("dayOfWeek", b.getSchedule().getDayOfWeek());
            map.put("startTime", b.getSchedule().getStartTime().toString());
            map.put("status", b.getStatus());
            return map;
        }).collect(Collectors.toList());
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> payload, HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");

        if (!user.getRole().equals("ADMIN")) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized")); // Format JSON
        }

        Optional<ClassBookingModel> bookingOpt = classBookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            ClassBookingModel booking = bookingOpt.get();
            booking.setStatus(payload.get("status"));
            classBookingRepository.save(booking);
            return ResponseEntity.ok(Map.of("message", "Status berhasil diupdate")); // Format JSON
        }
        return ResponseEntity.status(404).body(Map.of("message", "Data tidak ditemukan")); // Format JSON
    }

    @PostMapping("/classes")
    public ResponseEntity<?> addClass(@RequestBody ClassScheduleModel newClass, HttpServletRequest req) {
        try {
            UserModel user = (UserModel) req.getAttribute("user");
            if (!user.getRole().equals("ADMIN")) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            classService.createSchedule(newClass);
            return ResponseEntity.ok(Map.of("message", "Kelas berhasil ditambahkan!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id, HttpServletRequest req) {
        try {
            UserModel user = (UserModel) req.getAttribute("user");
            if (!user.getRole().equals("ADMIN")) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            classService.deleteSchedule(id);
            return ResponseEntity.ok().body(Map.of("message", "Kelas berhasil dihapus"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}