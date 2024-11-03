package com.example.tgbot.bot.repository;

import com.example.tgbot.bot.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
