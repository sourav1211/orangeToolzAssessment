package com.assessment.orangetoolz.repository;

import com.assessment.orangetoolz.model.ValidCustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidCustomerInfoRepository extends JpaRepository<ValidCustomerInfo, Long> {
    Long countByMobileNumberAndEmailAddressAndIsActiveTrue(String mobileNo,String email);
    List<ValidCustomerInfo> findAllByIsActiveTrue();
}
