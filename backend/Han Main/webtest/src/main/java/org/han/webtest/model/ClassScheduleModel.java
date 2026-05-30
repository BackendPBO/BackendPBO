package org.han.webtest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name="class_schedules")
public class ClassScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 20)
    private String dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(length = 100)
    private String trainerName;

    public ClassScheduleModel() {}

    public ClassScheduleModel(String serviceName, String dayOfWeek, LocalTime startTime, LocalTime endTime, String trainerName) {
        this.serviceName = serviceName;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.trainerName = trainerName;
    }
}