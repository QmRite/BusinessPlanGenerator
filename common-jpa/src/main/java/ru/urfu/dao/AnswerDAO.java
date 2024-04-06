package ru.urfu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.entity.Answer;
import ru.urfu.entity.AppUser;

public interface AnswerDAO extends JpaRepository<Answer, Long> {

}
