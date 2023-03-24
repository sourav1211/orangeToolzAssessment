package com.assessment.orangetoolz.repository;

import com.assessment.orangetoolz.model.InvalidCustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvalidCustomerInfoRepository extends JpaRepository<InvalidCustomerInfo, Long> {
    List<InvalidCustomerInfo> findAllByIsActiveTrue();
}
