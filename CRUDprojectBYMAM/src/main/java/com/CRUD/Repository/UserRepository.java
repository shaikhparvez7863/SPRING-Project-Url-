package com.CRUD.Repository;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CRUD.Entity.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    List<UserEntity> findByIdAndName(Integer id, String name);
    
    Page<UserEntity> findByRegistrationDateBetween(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    
    // Add method to find user by phone number
    Optional<UserEntity> findByPhone(String phone);
    List<UserEntity> findByRegistrationDateBetween(LocalDate fromDate, LocalDate toDate);
	Page<UserEntity> findAll(Pageable pageable);
}