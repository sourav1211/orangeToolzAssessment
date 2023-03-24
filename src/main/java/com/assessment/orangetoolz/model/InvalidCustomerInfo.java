package com.assessment.orangetoolz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Table(name = "invalid_customer_info")
@Entity
public class InvalidCustomerInfo extends BaseModel {


    @Column
    private String reasonOfInvalid;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String cityName;
    @Column
    private String stateName;
    @Column
    private String zipCode;
    @Column
    private String mobileNumber;
    @Column
    private String emailAddress;
    @Column
    private String ipAddress;
}


