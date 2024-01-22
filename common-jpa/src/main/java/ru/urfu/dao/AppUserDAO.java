package ru.urfu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
