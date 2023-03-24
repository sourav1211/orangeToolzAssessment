package com.assessment.orangetoolz.utils;


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
}
