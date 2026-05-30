package org.han.webtest.repository;

import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel;
import org.han.webtest.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ClassRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassScheduleRepository scheduleRepository;

    @Autowired
    private ClassBookingRepository bookingRepository;

    private UserModel savedUser;
    private ClassScheduleModel savedSchedule;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        scheduleRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(new UserModel("Budi", "budi@mail.com", "pass"));
        savedSchedule = scheduleRepository.save(new ClassScheduleModel(
                "Yoga", "MONDAY",
                LocalTime.of(8, 0), LocalTime.of(9, 0),
                "Trainer A"
        ));
    }

    @Test
    void testCariJadwalByHari() {
        scheduleRepository.save(new ClassScheduleModel(
                "Zumba", "MONDAY",
                LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Trainer B"
        ));
        scheduleRepository.save(new ClassScheduleModel(
                "Pilates", "TUESDAY",
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Trainer C"
        ));

        List<ClassScheduleModel> mondayClasses = scheduleRepository.findByDayOfWeek("MONDAY");
        assertThat(mondayClasses).hasSize(2);
    }

    @Test
    void testBookingKelas() {
        ClassBookingModel booking = new ClassBookingModel(
                savedUser, savedSchedule, LocalDate.now(), "PENDING"
        );
        bookingRepository.save(booking);

        List<ClassBookingModel> bookings = bookingRepository.findByUserId(savedUser.getId());
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void testCekDuplikatBooking() {
        // Booking pertama
        bookingRepository.save(new ClassBookingModel(
                savedUser, savedSchedule, LocalDate.now(), "PENDING"
        ));

        // Cek apakah sudah ada booking aktif (bukan REJECTED)
        boolean alreadyBooked = bookingRepository
                .existsByUserAndScheduleAndStatusNot(savedUser, savedSchedule, "REJECTED");

        assertThat(alreadyBooked).isTrue();
    }

    @Test
    void testBookingBolehJikaStatusRejected() {
        // Booking sebelumnya di-reject
        bookingRepository.save(new ClassBookingModel(
                savedUser, savedSchedule, LocalDate.now(), "REJECTED"
        ));

        // Cek: tidak ada booking aktif, jadi boleh booking lagi
        boolean alreadyBooked = bookingRepository
                .existsByUserAndScheduleAndStatusNot(savedUser, savedSchedule, "REJECTED");

        assertThat(alreadyBooked).isFalse();
    }
}
