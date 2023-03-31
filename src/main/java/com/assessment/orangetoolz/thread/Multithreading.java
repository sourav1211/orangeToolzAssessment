package com.assessment.orangetoolz.thread;

import com.assessment.orangetoolz.Enum.ReasonOfInvalid;
import com.assessment.orangetoolz.dto.CustomerInfoDto;
import com.assessment.orangetoolz.repository.InvalidCustomerInfoRepository;
import com.assessment.orangetoolz.repository.ValidCustomerInfoRepository;
import com.assessment.orangetoolz.services.FileManagementServices;
import com.assessment.orangetoolz.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Multithreading implements Runnable {
    private Integer threadNumber = 0;
    private Integer loopNumberStart = 0;
    private Integer loopNumberEnd = 0;
    private Integer idCounter = 0;

    @Autowired
    ValidCustomerInfoRepository validCustomerInfoRepository;
    @Autowired
    InvalidCustomerInfoRepository invalidCustomerInfoRepository;

    private CountDownLatch latch;
    private static List<String> lines = Collections.emptyList();

    static {
        try {
            String filePath = Utils.getAbsolutePathFromResource(
                    Utils.getValueFromPropertiesFile("textfile.name.main")
            );
            lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            System.out.println(lines.size());
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private FileManagementServices fileManagementServices;

    public Multithreading(Integer threadNumber, Integer loopNumberStart, Integer loopNumberEnd,
                          FileManagementServices fileManagementServices,
                          CountDownLatch latch, ValidCustomerInfoRepository validCustomerInfoRepository,
                          InvalidCustomerInfoRepository invalidCustomerInfoRepository
    ) {
        this.threadNumber = threadNumber;
        this.loopNumberStart = loopNumberStart;
        this.loopNumberEnd = loopNumberEnd;
        this.fileManagementServices = fileManagementServices;
        this.latch = latch;
        this.invalidCustomerInfoRepository = invalidCustomerInfoRepository;
        this.validCustomerInfoRepository = validCustomerInfoRepository;
    }


    public static Integer getFileLineCount() {
        return lines.size();
    }

//    @Override
//    public void run() {
//
//        System.out.println("calling me"+loopNumberStart+"-"+loopNumberEnd+" ::: "+threadNumber);
//        for (int i = loopNumberStart; i < loopNumberEnd+1; i++) {
////            System.out.println(i+" : I calling the thread number = "+ threadNumber);
//            try {
//                fileManagementServices.saveDateFromFileFromStringLIne(lines.get(i));
//            } catch (Exception e) {
////                System.out.println("Not found index: "+i);
//            }
//        }
//        latch.countDown();
//    }

    @Override
    public void run() {
        PreparedStatement preparedStatementValid;
        PreparedStatement preparedStatementInvalid;
        Date utilDate= new Date();
        java.sql.Date sqlDate= new java.sql.Date(utilDate.getTime());

        try {

            Connection connection = getDatabaseConnection();
            connection.setAutoCommit(true);

            String compiledQueryValid = "INSERT INTO orangetoolz.valid_customer_info (id, created_at, created_by, is_active,\n" +
                    "updated_at, updated_by, city_name, email_address, first_name, ip_address,\n" +
                    "last_name, mobile_number, state_name, zip_code)" +
                    " VALUES" + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String compiledQueryInvalid = "INSERT INTO orangetoolz.invalid_customer_info (id, created_at, created_by, is_active,\n" +
                    "updated_at, updated_by, city_name, email_address, first_name, ip_address,\n" +
                    "last_name, mobile_number, state_name, zip_code, reason_of_invalid)" +
                    " VALUES" + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            preparedStatementValid = connection.prepareStatement(compiledQueryValid);
            preparedStatementInvalid = connection.prepareStatement(compiledQueryInvalid);

            System.out.println("calling me" + loopNumberStart + "-" + loopNumberEnd + " ::: " + threadNumber);

            for (int i = loopNumberStart; i < loopNumberEnd + 1; i++) {
                idCounter = idCounter + 1;


//                System.out.println("Looping " + idCounter + "-" + loopNumberEnd + " ::: " + threadNumber);
                CustomerInfoDto dto = fileManagementServices.stringSeparatedBySymbol(lines.get(i), ",");
                Integer checkCustomerValidity = fileManagementServices.checkCustomerValidity(dto);
                if (checkCustomerValidity == 0) {
                    Long countDuplicate = validCustomerInfoRepository.countByMobileNumberAndEmailAddressAndIsActiveTrue(dto.getMobileNumber(), dto.getEmailAddress());
                    if (countDuplicate > 0) {
                        dto.setReasonOfInvalid(ReasonOfInvalid.DUPLICATE.name());
                        preparedStatementInvalid.setInt(1, i+1);
                        preparedStatementInvalid.setDate(2, sqlDate);
                        preparedStatementInvalid.setInt(3, 0);
                        preparedStatementInvalid.setInt(4, 1);
                        preparedStatementInvalid.setDate(5, null);
                        preparedStatementInvalid.setInt(6, 0);
                        preparedStatementInvalid.setString(7, dto.getCityName());
                        preparedStatementInvalid.setString(8, dto.getEmailAddress());
                        preparedStatementInvalid.setString(9, dto.getFirstName());
                        preparedStatementInvalid.setString(10, dto.getIpAddress());
                        preparedStatementInvalid.setString(11, dto.getLastName());
                        preparedStatementInvalid.setString(12, dto.getMobileNumber());
                        preparedStatementInvalid.setString(13, dto.getStateName());
                        preparedStatementInvalid.setString(14, dto.getZipCode());
                        preparedStatementInvalid.setString(15, dto.getReasonOfInvalid());
                        preparedStatementInvalid.addBatch();
                    } else {
                        preparedStatementValid.setInt(1, i+1);
                        preparedStatementValid.setDate(2, sqlDate);
                        preparedStatementValid.setInt(3, 0);
                        preparedStatementValid.setInt(4, 1);
                        preparedStatementValid.setDate(5, null);
                        preparedStatementValid.setInt(6, 0);
                        preparedStatementValid.setString(7, dto.getCityName());
                        preparedStatementValid.setString(8, dto.getEmailAddress());
                        preparedStatementValid.setString(9, dto.getFirstName());
                        preparedStatementValid.setString(10, dto.getIpAddress());
                        preparedStatementValid.setString(11, dto.getLastName());
                        preparedStatementValid.setString(12, dto.getMobileNumber());
                        preparedStatementValid.setString(13, dto.getStateName());
                        preparedStatementValid.setString(14, dto.getZipCode());
                        preparedStatementValid.addBatch();
                    }
                } else {
                    if (checkCustomerValidity == 1) {
                        dto.setReasonOfInvalid(ReasonOfInvalid.MOBILE_NUMBER.name());
                        preparedStatementInvalid.setInt(1, i+1);
                        preparedStatementInvalid.setDate(2,sqlDate);
                        preparedStatementInvalid.setInt(3, 0);
                        preparedStatementInvalid.setInt(4, 1);
                        preparedStatementInvalid.setDate(5, null);
                        preparedStatementInvalid.setInt(6, 0);
                        preparedStatementInvalid.setString(7, dto.getCityName());
                        preparedStatementInvalid.setString(8, dto.getEmailAddress());
                        preparedStatementInvalid.setString(9, dto.getFirstName());
                        preparedStatementInvalid.setString(10, dto.getIpAddress());
                        preparedStatementInvalid.setString(11, dto.getLastName());
                        preparedStatementInvalid.setString(12, dto.getMobileNumber());
                        preparedStatementInvalid.setString(13, dto.getStateName());
                        preparedStatementInvalid.setString(14, dto.getZipCode());
                        preparedStatementInvalid.setString(15, dto.getReasonOfInvalid());
                        preparedStatementInvalid.addBatch();
                    }
                    if (checkCustomerValidity == 2) {
                        dto.setReasonOfInvalid(ReasonOfInvalid.EMAIL_ADDRESS.name());
                        preparedStatementInvalid.setInt(1, i+1);
                        preparedStatementInvalid.setDate(2, sqlDate);
                        preparedStatementInvalid.setInt(3, 0);
                        preparedStatementInvalid.setInt(4, 1);
                        preparedStatementInvalid.setDate(5, null);
                        preparedStatementInvalid.setInt(6, 0);
                        preparedStatementInvalid.setString(7, dto.getCityName());
                        preparedStatementInvalid.setString(8, dto.getEmailAddress());
                        preparedStatementInvalid.setString(9, dto.getFirstName());
                        preparedStatementInvalid.setString(10, dto.getIpAddress());
                        preparedStatementInvalid.setString(11, dto.getLastName());
                        preparedStatementInvalid.setString(12, dto.getMobileNumber());
                        preparedStatementInvalid.setString(13, dto.getStateName());
                        preparedStatementInvalid.setString(14, dto.getZipCode());
                        preparedStatementInvalid.setString(15, dto.getReasonOfInvalid());
                        preparedStatementInvalid.addBatch();
                    }
                }
                if(i!=0 && i%200==0){

                    preparedStatementValid.executeBatch();
                    preparedStatementInvalid.executeBatch();

                    preparedStatementValid.close();
                    preparedStatementInvalid.close();

                    preparedStatementValid = connection.prepareStatement(compiledQueryValid);
                    preparedStatementInvalid = connection.prepareStatement(compiledQueryInvalid);

                    System.out.println("inserted 200 data : "+i);
                    try {
                        Thread.sleep(2000);
                        System.out.println("slept 2s after 200");
                    }catch (InterruptedException e){}
                }
            }
            long start = System.currentTimeMillis();
            int[] insertedValid = preparedStatementValid.executeBatch();
            int[] insertedInvalid = preparedStatementInvalid.executeBatch();
            long end = System.currentTimeMillis();

            preparedStatementValid.close();
            preparedStatementInvalid.close();
            connection.close();
            System.out.println("total time taken to insert the batch = " + (end - start) + " ms");
            try {
                Thread.sleep(10000);
                System.out.println("slept 10s");
            }catch (InterruptedException e){}

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error found");
        }
        latch.countDown();
    }

    private Connection getDatabaseConnection() throws IOException, SQLException {
        String dbUrl = Utils.getValueFromPropertiesFile("spring.datasource.url");
        String username = Utils.getValueFromPropertiesFile("spring.datasource.username");
        String password = Utils.getValueFromPropertiesFile("spring.datasource.password");
        return DriverManager.getConnection(dbUrl, username, password);
    }
}
