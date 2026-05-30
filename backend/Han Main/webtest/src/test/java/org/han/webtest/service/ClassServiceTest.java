package org.han.webtest.service;

import org.han.webtest.model.ClassBookingModel;
import org.han.webtest.model.ClassScheduleModel;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.ClassBookingRepository;
import org.han.webtest.repository.ClassScheduleRepository;
import org.han.webtest.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassServiceTest {

    @Mock
    private ClassScheduleRepository scheduleRepository;

    @Mock
    private ClassBookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClassService classService;

    @Test
    void testGetAllSchedules() {
        List<ClassScheduleModel> schedules = List.of(
                new ClassScheduleModel("Yoga", "MONDAY", LocalTime.of(8,0), LocalTime.of(9,0), "Trainer A"),
                new ClassScheduleModel("Zumba", "TUESDAY", LocalTime.of(10,0), LocalTime.of(11,0), "Trainer B")
        );
        when(scheduleRepository.findAll()).thenReturn(schedules);

        List<ClassScheduleModel> result = classService.getAllSchedules();

        assertThat(result).hasSize(2);
    }

    @Test
    void testGetSchedulesByDay() {
        List<ClassScheduleModel> mondaySchedules = List.of(
                new ClassScheduleModel("Yoga", "MONDAY", LocalTime.of(8,0), LocalTime.of(9,0), "Trainer A")
        );
        when(scheduleRepository.findByDayOfWeek("MONDAY")).thenReturn(mondaySchedules);

        List<ClassScheduleModel> result = classService.getSchedulesByDay("MONDAY");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    void testBookClass_Berhasil() {
        UserModel user = new UserModel("Budi", "budi@mail.com", "pass");
        ClassScheduleModel schedule = new ClassScheduleModel("Yoga", "MONDAY", LocalTime.of(8,0), LocalTime.of(9,0), "Trainer A");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.existsByUserAndScheduleAndStatusNot(user, schedule, "REJECTED")).thenReturn(false);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ClassBookingModel result = classService.bookClass(1L, 1L, LocalDate.now());

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void testBookClass_SudahDibooking() {
        UserModel user = new UserModel("Budi", "budi@mail.com", "pass");
        ClassScheduleModel schedule = new ClassScheduleModel("Yoga", "MONDAY", LocalTime.of(8,0), LocalTime.of(9,0), "Trainer A");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(bookingRepository.existsByUserAndScheduleAndStatusNot(user, schedule, "REJECTED")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                classService.bookClass(1L, 1L, LocalDate.now())
        );

        assertThat(ex.getMessage()).contains("sudah membooking");
    }

    @Test
    void testBookClass_UserTidakAda() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                classService.bookClass(99L, 1L, LocalDate.now())
        );

        assertThat(ex.getMessage()).isEqualTo("User not found");
    }
}
