package com.tugas_day21.repository;

import com.tugas_day21.model.UserDAO;
import com.tugas_day21.model.UserDetDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetRepo extends JpaRepository<UserDetDAO, Long> {
    UserDetDAO findByUserDAO(UserDAO userDAO);
}
