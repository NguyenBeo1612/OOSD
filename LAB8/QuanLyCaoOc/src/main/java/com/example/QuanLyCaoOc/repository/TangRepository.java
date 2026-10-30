package com.example.QuanLyCaoOc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.QuanLyCaoOc.entity.Tang;

public interface TangRepository
        extends JpaRepository<Tang, String> {
}
