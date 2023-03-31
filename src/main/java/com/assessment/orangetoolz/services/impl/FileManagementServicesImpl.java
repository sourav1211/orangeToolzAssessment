package com.assessment.orangetoolz.services.impl;

import com.assessment.orangetoolz.Enum.ReasonOfInvalid;
import com.assessment.orangetoolz.dto.CustomerInfoDto;
import com.assessment.orangetoolz.dto.Response;
import com.assessment.orangetoolz.model.InvalidCustomerInfo;
import com.assessment.orangetoolz.model.ValidCustomerInfo;
import com.assessment.orangetoolz.repository.InvalidCustomerInfoRepository;
import com.assessment.orangetoolz.repository.ValidCustomerInfoRepository;
import com.assessment.orangetoolz.services.FileManagementServices;
import com.assessment.orangetoolz.thread.Multithreading;
import com.assessment.orangetoolz.utils.ResponseBuilder;
import com.assessment.orangetoolz.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class FileManagementServicesImpl implements FileManagementServices {

    @Autowired
    ValidCustomerInfoRepository validCustomerInfoRepository;
    @Autowired
    InvalidCustomerInfoRepository invalidCustomerInfoRepository;

    public FileManagementServicesImpl(ValidCustomerInfoRepository validCustomerInfoRepository, InvalidCustomerInfoRepository invalidCustomerInfoRepository) {
        this.validCustomerInfoRepository = validCustomerInfoRepository;
        this.invalidCustomerInfoRepository = invalidCustomerInfoRepository;
    }
    @Override
    public Boolean callToSateData(String stringLine) {
        saveDateFromFileFromStringLIne(stringLine);
        return true;
    }

    @Override
    public Response saveDataFromFileWithMultithreadingNew() {
        try {
//            System.out.println("start calling");
            Integer loopCount = Multithreading.getFileLineCount();
//            Integer divisionNumber = 10;
            Integer divisionNumber = 10000;
            double numberOfThreads=Math.ceil(loopCount / divisionNumber);
            CountDownLatch latch = new CountDownLatch((int) numberOfThreads+1);
            Integer startNumber = 0;
            Integer endNumber = 0;
            for (int i = 0; i < numberOfThreads + 1; i++) {
                if (i == 0) {
                    startNumber = 0;
                    endNumber = divisionNumber - 1;
                } else {
                    startNumber = startNumber + divisionNumber;
                    endNumber = (endNumber + divisionNumber);
                }
                Multithreading multithreading = new Multithreading(i, startNumber, endNumber, this,latch,validCustomerInfoRepository,invalidCustomerInfoRepository);
                Thread callToSave = new Thread(multithreading);
                callToSave.start();
                callToSave.join();
                callToSave.stop();
            }
            latch.await();
//            System.out.println("Finally call");
            writeValidCustomersOnFile();
            writeInvalidCustomersOnFile();
//            System.out.println("End calling");

        }catch (Exception e){return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File read successfully");}
        System.out.println("End Process");
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "File read successfully");
    }
    @Override
    public Response saveDataFromFileTest() {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get("F:\\1M-customers - Copy.txt"), StandardCharsets.UTF_8);
//            lines = Files.readAllLines(Paths.get("F:\\1M-customers - Copy.txt"), StandardCharsets.UTF_8);
            System.out.println("get");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response saveDataFromFile() throws IOException, URISyntaxException {
//        URL res = getClass().getClassLoader().getResource("1M-customers - Copy.txt");
//        File file = Paths.get(res.toURI()).toFile();
//        String absolutePath = file.getAbsolutePath();

        BufferedReader br = new BufferedReader(new FileReader(Utils.getAbsolutePathFromResource("1M-customers - Copy.txt")));
//        BufferedReader br = new BufferedReader(new FileReader("F:\\1M-customers - Copy.txt"));
//        BufferedReader br = new BufferedReader(new FileReader("F:\\1M-customers.txt"));
        try {
            String line = br.readLine();
            System.out.println("start");
            while (line != null) {
                saveDateFromFileFromStringLIne(line);
                line = br.readLine();
            }
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, null, "File read successfully");
        } finally {
            br.close();
            writeValidCustomersOnFile();
            writeInvalidCustomersOnFile();

        }
//        return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error");
    }

    @Override
    public void saveDateFromFileFromStringLIne(String singleLine) {
        CustomerInfoDto dto = stringSeparatedBySymbol(singleLine, ",");
        Integer checkCustomerValidity = checkCustomerValidity(dto);
        if (checkCustomerValidity == 0) {
            Long countDuplicate = validCustomerInfoRepository.countByMobileNumberAndEmailAddressAndIsActiveTrue(dto.getMobileNumber(), dto.getEmailAddress());
            if (countDuplicate > 0) {
                dto.setReasonOfInvalid(ReasonOfInvalid.DUPLICATE.name());
                saveInvalidCustomers(dto);
            } else {
                saveValidCustomers(dto);
            }
        } else {
            if (checkCustomerValidity == 1) {
                dto.setReasonOfInvalid(ReasonOfInvalid.MOBILE_NUMBER.name());
                saveInvalidCustomers(dto);
            }
            if (checkCustomerValidity == 2) {
                dto.setReasonOfInvalid(ReasonOfInvalid.EMAIL_ADDRESS.name());
                saveInvalidCustomers(dto);
            }

        }
//        return ResponseBuilder.getSuccessResponse(HttpStatus.OK,null,"File read successfully");

    }


    synchronized private void saveInvalidCustomers(CustomerInfoDto dto) {
        InvalidCustomerInfo invalidCustomerInfo = new InvalidCustomerInfo();
        invalidCustomerInfo.setFirstName(dto.getFirstName());
        invalidCustomerInfo.setLastName(dto.getLastName());
        invalidCustomerInfo.setCityName(dto.getCityName());
        invalidCustomerInfo.setStateName(dto.getStateName());
        invalidCustomerInfo.setZipCode(dto.getZipCode());
        invalidCustomerInfo.setMobileNumber(dto.getMobileNumber());
        invalidCustomerInfo.setEmailAddress(dto.getEmailAddress());
        invalidCustomerInfo.setIpAddress(dto.getIpAddress());
        invalidCustomerInfo.setReasonOfInvalid(dto.getReasonOfInvalid());
        invalidCustomerInfoRepository.save(invalidCustomerInfo);
    }

    synchronized private void saveValidCustomers(CustomerInfoDto dto) {
        ValidCustomerInfo validInfo = new ValidCustomerInfo();
        validInfo.setFirstName(dto.getFirstName());
        validInfo.setLastName(dto.getLastName());
        validInfo.setCityName(dto.getCityName());
        validInfo.setStateName(dto.getStateName());
        validInfo.setZipCode(dto.getZipCode());
        validInfo.setMobileNumber(dto.getMobileNumber());
        validInfo.setEmailAddress(dto.getEmailAddress());
        validInfo.setIpAddress(dto.getIpAddress());
        validCustomerInfoRepository.save(validInfo);
    }

@Override
    public Integer checkCustomerValidity(CustomerInfoDto dto) {

        /*
         * Customer validity check by mobile number and email
         * --------------------------------------------------------
         * return 0 : valid customer
         * return 1 : invalid customer for mobile number
         * return 2 : invalid customer for email address
         * */
        Boolean isMobileNumberValid = isMobileNumberValid(dto.getMobileNumber());
        Boolean isEmailValid = isEmailValid(dto.getEmailAddress());
        if (isMobileNumberValid && isEmailValid) {
            return 0;
        } else {
            if (!isMobileNumberValid) {
                return 1;
            }
            if (!isEmailValid) {
                return 2;
            }
        }
        return null;
    }
    @Override
    public CustomerInfoDto stringSeparatedBySymbol(String line, String separatorElement) {
//        List<String> lineElementList = Arrays.asList(line.split(separatorElement));
        String[] lineElementList = line.split(separatorElement);
        CustomerInfoDto dto = new CustomerInfoDto();
        dto.setFirstName(lineElementList[0]);
        dto.setLastName(lineElementList[1]);
        dto.setCityName(lineElementList[2]);
        dto.setStateName(lineElementList[3]);
        dto.setZipCode(lineElementList[4]);
        dto.setMobileNumber(lineElementList[5]);
        dto.setEmailAddress(lineElementList[6]);
        try {
            dto.setIpAddress(lineElementList[7]);
        } catch (ArrayIndexOutOfBoundsException e) {
            dto.setIpAddress("Not found");
            e.printStackTrace();
        }

        return dto;
    }


    private Boolean isMobileNumberValid(String mobileNo) {
        /*Check validity for : 555-555-5555                                 ^(\d{3}\-){2}\d{4}$             */
        String pattern01 = "^(\\d{3}\\-){2}\\d{4}$";

        /*Check validity for : (555)555-5555 or (555) 555-5555              ^\((\d{3}\)[ ]?)\d{3}\-\d{4}$   */
        String pattern02 = "^\\((\\d{3}\\)[ ]?)\\d{3}\\-\\d{4}$";

        /*Check validity for : 555 555 5555                                 ^(\d{3}\ ){2}\d{4}$             */
        String pattern03 = "^(\\d{3}\\ ){2}\\d{4}$";

        /*Check validity for : 5555555555                                   ^\d{10}$                        */
        String pattern04 = "^\\d{10}$";

        /*Check validity for : 1 555 555 5555                               ^(\d{1})\ (\d{3}\ ){2}\d{4}$    */
        String pattern05 = "^(\\d{1})\\ (\\d{3}\\ ){2}\\d{4}$";

        return
                (
                        Utils.isMobileNumberValidByPattern(mobileNo, pattern01)
                                || Utils.isMobileNumberValidByPattern(mobileNo, pattern02)
                                || Utils.isMobileNumberValidByPattern(mobileNo, pattern03)
                                || Utils.isMobileNumberValidByPattern(mobileNo, pattern04)
                                || Utils.isMobileNumberValidByPattern(mobileNo, pattern05)
                );


    }

    private static Boolean isEmailValid(String emailAddress) {
        return Utils.isEmailValid(emailAddress);
    }

    private void writeValidCustomersOnFile() {
        List<ValidCustomerInfo> infoList = validCustomerInfoRepository.findAllByIsActiveTrue();
        StringBuilder customersLines = new StringBuilder();
        Integer counter = 0;
        Integer count100k = 0;
        Integer fileCounter = 0;
        for (ValidCustomerInfo validCustomerInfo : infoList) {
            CustomerInfoDto dto = setDto(validCustomerInfo);
            StringBuilder customer = setCustomer(dto, counter);
            customersLines.append(customer);
            customersLines.append(System.lineSeparator());
            counter = counter + 1;
            count100k = count100k + 1;
            if (count100k == 100000) {
                fileCounter = fileCounter + 1;
                customersLines.append(System.lineSeparator());
                customersLines.append(System.lineSeparator());
                customersLines.append("[ Total valid customers in this file: " + count100k + " ]");
                count100k = 0;
                writeToFile(customersLines, "valid", fileCounter);
                customersLines = new StringBuilder();
            }
            if (infoList.size() == counter) {
                customersLines.append(System.lineSeparator());
                customersLines.append(System.lineSeparator());
                customersLines.append("[ This is last file. ]");
                customersLines.append(System.lineSeparator());
                customersLines.append("[ Total file (including this file): " + (fileCounter + 1) + " ]");
                customersLines.append(System.lineSeparator());
                customersLines.append("[ Total valid Customers in this file: " + count100k + " ]");
            }
        }
        writeToFile(customersLines, "valid", fileCounter + 1);
    }

    private void writeInvalidCustomersOnFile() {
        List<InvalidCustomerInfo> infoList = invalidCustomerInfoRepository.findAllByIsActiveTrue();
        StringBuilder customersLines = new StringBuilder();
        Integer counter = 0;
        for (InvalidCustomerInfo invalidCustomerInfo : infoList) {
            CustomerInfoDto dto = setDto(invalidCustomerInfo);
            StringBuilder customer = setCustomer(dto, counter);
            customersLines.append(customer);
            customersLines.append(System.lineSeparator());
            counter = counter + 1;
            if (infoList.size() == counter) {
                customersLines.append(System.lineSeparator());
                customersLines.append(System.lineSeparator());
                customersLines.append("[ Total Invalid Customers: " + counter + " ]");
            }
        }
        writeToFile(customersLines, "invalid", 0);
//        try {
//            FileWriter myWriter = new FileWriter("F:\\output\\invalid_customer_list.txt");
//            myWriter.write(customersLines.toString());
//            myWriter.close();
//            System.out.println("Successfully wrote to the invalid_customer_list file.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
    }

    private CustomerInfoDto setDto(Object obj) {
        CustomerInfoDto dto = new CustomerInfoDto();
        if (obj instanceof ValidCustomerInfo) {
            dto.setFirstName(((ValidCustomerInfo) obj).getFirstName());
            dto.setLastName(((ValidCustomerInfo) obj).getLastName());
            dto.setCityName(((ValidCustomerInfo) obj).getCityName());
            dto.setStateName(((ValidCustomerInfo) obj).getStateName());
            dto.setZipCode(((ValidCustomerInfo) obj).getZipCode());
            dto.setMobileNumber(((ValidCustomerInfo) obj).getMobileNumber());
            dto.setEmailAddress(((ValidCustomerInfo) obj).getEmailAddress());
            dto.setIpAddress(((ValidCustomerInfo) obj).getIpAddress());
            return dto;
        }
        if (obj instanceof InvalidCustomerInfo) {
            dto.setFirstName(((InvalidCustomerInfo) obj).getFirstName());
            dto.setLastName(((InvalidCustomerInfo) obj).getLastName());
            dto.setCityName(((InvalidCustomerInfo) obj).getCityName());
            dto.setStateName(((InvalidCustomerInfo) obj).getStateName());
            dto.setZipCode(((InvalidCustomerInfo) obj).getZipCode());
            dto.setMobileNumber(((InvalidCustomerInfo) obj).getMobileNumber());
            dto.setEmailAddress(((InvalidCustomerInfo) obj).getEmailAddress());
            dto.setIpAddress(((InvalidCustomerInfo) obj).getIpAddress());
            dto.setReasonOfInvalid(((InvalidCustomerInfo) obj).getReasonOfInvalid());
            return dto;
        }
        return null;

    }

    private StringBuilder setCustomer(CustomerInfoDto dto, Integer counter) {
        StringBuilder customer = new StringBuilder();
        customer.append("[ Sl No : " + (counter + 1) + " ] ");
        customer.append("First Name: " + dto.getFirstName());
        customer.append(", ");
        customer.append("Last Name: " + dto.getLastName());
        customer.append(", ");
        customer.append("City Name: " + dto.getCityName());
        customer.append(", ");
        customer.append("State Name: " + dto.getStateName());
        customer.append(", ");
        customer.append("Zip Code: " + dto.getZipCode());
        customer.append(", ");
        customer.append("Mobile Number: " + dto.getMobileNumber());
        customer.append(", ");
        customer.append("Email Address: " + dto.getEmailAddress());
        customer.append(", ");
        customer.append("IP Address: " + dto.getIpAddress());
        if (dto.getReasonOfInvalid() != null) {
            customer.append(", ");
            customer.append("Reason of Invalid: " + dto.getReasonOfInvalid());
        }
        return customer;
    }

    private void writeToFile(StringBuilder customer, String customerType, Integer fileCounter) {
        String fileExtension = ".txt";
        String fileDir = "F:\\output\\";
        String fileName = (customerType == "valid" ? "valid_customer_list_" + fileCounter : "invalid_customer_list");
        try {
            FileWriter myWriter = new FileWriter(fileDir + fileName + fileExtension);
            myWriter.write(customer.toString());
            myWriter.close();
//            System.out.println("Successfully wrote "+fileName+" file");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
