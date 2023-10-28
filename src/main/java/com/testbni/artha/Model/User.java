package com.testbni.artha.Model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;
    
    private String mobileNumber;

    //cc
    private Integer cardNumber;
    private Integer CVV;
    private Date expiredDate;
    private String cardHolderName;

    private boolean isEnabled;
}
