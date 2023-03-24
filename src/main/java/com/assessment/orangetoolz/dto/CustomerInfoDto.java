package com.assessment.orangetoolz.dto;

import lombok.Data;

@Data
public class CustomerInfoDto {
    private Long id;
    
    private String firstName;
    
    private String lastName;
    
    private String cityName;
    
    private String stateName;
    
    private String zipCode;
    
    private String mobileNumber;
    
    private String emailAddress;
    
    private String ipAddress;

    private String reasonOfInvalid;


    public static CustomerInfoDto getInstance(){
        return new CustomerInfoDto();
    }
}
