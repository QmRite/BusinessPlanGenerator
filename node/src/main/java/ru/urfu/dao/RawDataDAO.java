package ru.urfu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
