package ru.urfu.service.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.service.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
