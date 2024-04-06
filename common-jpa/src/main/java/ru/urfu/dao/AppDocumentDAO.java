package ru.urfu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.urfu.entity.AppDocument;


public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
