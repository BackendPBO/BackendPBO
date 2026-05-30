package org.han.webtest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name="class_bookings")
public class ClassBookingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassScheduleModel schedule;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    public ClassBookingModel() {}

    public ClassBookingModel(UserModel user, ClassScheduleModel schedule, LocalDate bookingDate, String status) {
        this.user = user;
        this.schedule = schedule;
        this.bookingDate = bookingDate;
        this.status = status;
    }
}