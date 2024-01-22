package ru.urfu.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.service.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
