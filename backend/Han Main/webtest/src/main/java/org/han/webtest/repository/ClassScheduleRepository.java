package org.han.webtest.repository;

import org.han.webtest.model.ClassScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassScheduleModel, Long> {

    List<ClassScheduleModel> findByDayOfWeek(String dayOfWeek);

}