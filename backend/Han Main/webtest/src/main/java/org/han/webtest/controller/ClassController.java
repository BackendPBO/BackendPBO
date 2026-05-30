package org.han.webtest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel;
import org.han.webtest.model.UserModel;
import org.han.webtest.service.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public List<ClassScheduleModel> getAllSchedules() {
        return classService.getAllSchedules();
    }

    @GetMapping("/day/{dayOfWeek}")
    public List<ClassScheduleModel> getSchedulesByDay(@PathVariable String dayOfWeek) {
        return classService.getSchedulesByDay(dayOfWeek);
    }

    @PostMapping("/book/{scheduleId}")
    public ResponseEntity<?> bookClass(@PathVariable Long scheduleId, @RequestBody Map<String, String> payload, HttpServletRequest req) {
        try {
            UserModel user = (UserModel) req.getAttribute("user");

            LocalDate bookingDate = payload.containsKey("bookingDate")
                    ? LocalDate.parse(payload.get("bookingDate"))
                    : LocalDate.now();

            classService.bookClass(user.getId(), scheduleId, bookingDate);

            return ResponseEntity.ok(Map.of("message", "Booking berhasil disubmit!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<ClassBookingModel>> getMyBookings(HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");
        List<ClassBookingModel> myBookings = classService.getUserBookings(user.getId());
        return ResponseEntity.ok(myBookings);
    }
}