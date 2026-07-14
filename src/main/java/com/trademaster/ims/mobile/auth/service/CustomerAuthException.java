package com.trademaster.ims.mobile.auth.service;
import org.springframework.http.HttpStatus;
public class CustomerAuthException extends RuntimeException{
 private final HttpStatus status; private final String code;
 public CustomerAuthException(HttpStatus status,String code,String message){super(message);this.status=status;this.code=code;}
 public HttpStatus status(){return status;} public String code(){return code;}
}
