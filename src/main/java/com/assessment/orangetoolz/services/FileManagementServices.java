package com.assessment.orangetoolz.services;

import com.assessment.orangetoolz.dto.CustomerInfoDto;
import com.assessment.orangetoolz.dto.Response;

import java.io.IOException;

public interface FileManagementServices {
    Response saveDataFromFile() throws IOException;
}