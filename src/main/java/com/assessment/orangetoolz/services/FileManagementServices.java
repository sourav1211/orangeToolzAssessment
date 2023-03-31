package com.assessment.orangetoolz.services;

import com.assessment.orangetoolz.dto.CustomerInfoDto;
import com.assessment.orangetoolz.dto.Response;

import java.io.IOException;
import java.net.URISyntaxException;

public interface FileManagementServices {
    Boolean callToSateData(String stringLine);
    Response saveDataFromFile() throws IOException, URISyntaxException;
    Response saveDataFromFileTest();
    Response saveDataFromFileWithMultithreadingNew();
    void saveDateFromFileFromStringLIne(String stringLine);
    CustomerInfoDto stringSeparatedBySymbol(String line, String separatorElement);
    Integer checkCustomerValidity(CustomerInfoDto dto);
}