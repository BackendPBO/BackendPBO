package org.han.webtest.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.ClassBookingRepository;
import org.han.webtest.repository.ClassScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trainer")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TrainerController {

    private final ClassScheduleRepository classScheduleRepository;
    private final ClassBookingRepository classBookingRepository;

    public TrainerController(ClassScheduleRepository classScheduleRepository, ClassBookingRepository classBookingRepository) {
        this.classScheduleRepository = classScheduleRepository;
        this.classBookingRepository = classBookingRepository;
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getTrainerDashboard(HttpServletRequest req) {
        UserModel user = (UserModel) req.getAttribute("user");
        if (user == null || !user.getRole().equals("TRAINER")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String trainerName = user.getUsername();

        List<ClassScheduleModel> trainerClasses = classScheduleRepository.findAll().stream()
                .filter(c -> c.getTrainerName().equalsIgnoreCase(trainerName))
                .collect(Collectors.toList());

        List<Map<String, Object>> response = trainerClasses.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceName", c.getServiceName());
            map.put("dayOfWeek", c.getDayOfWeek());
            map.put("startTime", c.getStartTime().toString());
            map.put("endTime", c.getEndTime().toString());

            List<ClassBookingModel> approvedBookings = classBookingRepository.findAll().stream()
                    .filter(b -> b.getSchedule().getId() == c.getId() && "APPROVED".equals(b.getStatus()))
                    .collect(Collectors.toList());

            map.put("bookedCount", approvedBookings.size());

            List<Map<String, String>> participants = approvedBookings.stream().map(b -> {
                Map<String, String> pMap = new HashMap<>();
                pMap.put("username", b.getUser().getUsername());
                return pMap;
            }).collect(Collectors.toList());

            map.put("participants", participants);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}