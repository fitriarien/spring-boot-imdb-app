package com.tugas_day21.repository;

import com.tugas_day21.model.UserDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<UserDAO, Long> {
    UserDAO findByEmail(String email);
}