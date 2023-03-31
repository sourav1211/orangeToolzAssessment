package com.assessment.orangetoolz.utils;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String patternForEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static Boolean isMobileNumberValidByPattern(String mobileNo, String pattern){
        return patternMatcher(mobileNo,pattern);
//        Pattern configurePattern = Pattern.compile(pattern);
//        Matcher checkPattern = configurePattern.matcher(mobileNo);
//        return (checkPattern.find() && checkPattern.group().equals(checkPattern));
    }
    public static Boolean isEmailValid(String email){
        return patternMatcher(email ,patternForEmail);
    }

    private static Boolean patternMatcher(String text, String pattern){
        Pattern configurePattern = Pattern.compile(pattern);
        Matcher checkPattern = configurePattern.matcher(text);
        return checkPattern.matches();
    }
    public static String getAbsolutePathFromResource(String fileName) {
        try {
            URL res = Utils.class.getClassLoader().getResource(fileName);
            File file = Paths.get(res.toURI()).toFile();
            String absolutePath = file.getAbsolutePath();
//        File file = new File("resources/"+fileName);
//        return file.getAbsolutePath();
//        String absolutePath = file.getAbsolutePath();
            return absolutePath;
        }catch (Exception e){}
        return "";
    }
    public static String getValueFromPropertiesFile(String elementName) throws IOException {
        FileReader reader=new FileReader(getAbsolutePathFromResource("application.properties"));

        Properties p=new Properties();
        p.load(reader);

//        System.out.println(p.getProperty("user"));
//        System.out.println(p.getProperty("password"));
        return p.getProperty(elementName);
    }
}
