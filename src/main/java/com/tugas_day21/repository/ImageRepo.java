package com.tugas_day21.repository;

import com.tugas_day21.model.ImageDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends JpaRepository<ImageDAO, String> {
}
