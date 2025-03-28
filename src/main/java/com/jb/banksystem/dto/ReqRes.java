package com.jb.banksystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jb.banksystem.entity.OurUsers;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {
    private int statusCode;
    private String error;
    private String message;
    private String token; // Access token
    private String refreshToken; // Refresh token
    private String expirationTime; // Thời gian hết hạn của access token (token chính)
    private String email;
    private String username;
    private String role;
    // Không cần password trong response
    private String password;  // Chỉ dùng trong request
    private OurUsers ourUsers;
    private List<OurUsers> ourUsersList;
}
