package com.assessment.orangetoolz.controller;

import com.assessment.orangetoolz.dto.Response;
import com.assessment.orangetoolz.services.CustomerInfoServices;
import com.assessment.orangetoolz.services.FileManagementServices;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

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
    public Response readFromFileAndSave() throws IOException, URISyntaxException {
        return fileManagementServices.saveDataFromFile();
    }
    @RequestMapping("/read-file-mt")
    public Response readFromFileAndSaveWithMultithreading () throws IOException {
        return fileManagementServices.saveDataFromFileWithMultithreadingNew();
    }
}
