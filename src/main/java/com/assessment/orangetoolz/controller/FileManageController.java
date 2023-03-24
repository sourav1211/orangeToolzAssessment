package com.assessment.orangetoolz.controller;

import com.assessment.orangetoolz.dto.CustomerInfoDto;
import com.assessment.orangetoolz.dto.Response;
import com.assessment.orangetoolz.services.CustomerInfoServices;
import com.assessment.orangetoolz.services.FileManagementServices;
import com.assessment.orangetoolz.web.FileUploadServlet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@RestController
@AllArgsConstructor
@NoArgsConstructor
public class FileManageController {

    @Autowired
    private CustomerInfoServices services;

    @Autowired
    private FileManagementServices fileManagementServices;

//    @RequestMapping("/hello")
//    public String index() {
//        return "start";
//    }
//
//    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        FileUploadServlet dd = new FileUploadServlet();
//        dd.doPost(request, response);
//        return null;
//    }

    @RequestMapping("/read-file")
    public Response readFromFileAndSave() throws IOException {
        return fileManagementServices.saveDataFromFile();
    }
}
