package com.jb.banksystem.service;

import com.jb.banksystem.dto.ReqRes;
import com.jb.banksystem.entity.OurUsers;
import com.jb.banksystem.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            // Kiểm tra xem có người dùng với username đã tồn tại không
            Optional<OurUsers> existingUserByUsername = usersRepo.findByUsername(registrationRequest.getUsername());
            if (existingUserByUsername.isPresent()) {
                reqRes.setStatusCode(400);
                reqRes.setError("Username already exists!");
                return reqRes;
            }

            // Kiểm tra xem có người dùng với email đã tồn tại không
            Optional<OurUsers> existingUserByEmail = usersRepo.findByEmail(registrationRequest.getEmail());
            if (existingUserByEmail.isPresent()) {
                reqRes.setStatusCode(400);
                reqRes.setError("Email already exists!");
                return reqRes;
            }

            OurUsers ourUser = new OurUsers();
            ourUser.setUsername(registrationRequest.getUsername());
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setEnabled(true);
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OurUsers ourUsersResult = usersRepo.save(ourUser);
            // Loại bỏ password trước khi trả về trong ReqRes
            ourUsersResult.setPassword(null); // Đảm bảo password không được trả về
            if (ourUsersResult.getId() > 0) {
                reqRes.setOurUsers(ourUsersResult);
                reqRes.setMessage("Successfully registered user");
                reqRes.setStatusCode(200);
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                throw new IllegalArgumentException("Username or Password cannot be null");
            }
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByUsername(loginRequest.getUsername()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(user);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRole(user.getRole());
            reqRes.setRefreshToken(refreshToken);
            reqRes.setExpirationTime("7Days");
            reqRes.setMessage("Successfully logged in");
        } catch (IllegalArgumentException e) {
            reqRes.setStatusCode(400);  // Bad Request
            reqRes.setError("Invalid input: " + e.getMessage());
        } catch (BadCredentialsException e) {
            reqRes.setStatusCode(401);  // Unauthorized
            reqRes.setError("Invalid credentials: " + e.getMessage());
        } catch (UsernameNotFoundException e) {
            reqRes.setStatusCode(404);  // Not Found
            reqRes.setError("User not found: " + e.getMessage());
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError("Internal server error: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            String token = refreshTokenRequest.getToken();

            // Kiểm tra token có đúng là refresh token không
            String tokenType = jwtUtils.extractType(token);
            if (!"refresh".equals(tokenType)) {
                reqRes.setStatusCode(403);
                reqRes.setMessage("Invalid token type. Only refresh tokens are allowed here.");
                return reqRes;
            }

            String username = jwtUtils.extractUsername(token);
            OurUsers user = usersRepo.findByUsername(username).orElseThrow();

            if (jwtUtils.isTokenValid(token, user)) {
                String newAccessToken = jwtUtils.generateToken(user);
                reqRes.setStatusCode(200);
                reqRes.setToken(newAccessToken);
                reqRes.setRefreshToken(token);
                reqRes.setExpirationTime("7Days");
                reqRes.setMessage("Successfully refreshed token");
            } else {
                reqRes.setStatusCode(403);
                reqRes.setMessage("Refresh token expired or invalid");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }


    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();
        try {
            List<OurUsers> results = usersRepo.findAll();
            if (!results.isEmpty()) {
                reqRes.setStatusCode(200);
                reqRes.setOurUsersList(results);
                reqRes.setMessage("Successfully retrieved all users");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUserById(Long id) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers ourUser = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
            reqRes.setOurUsers(ourUser);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Successfully retrieved user with id " + id);
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Long id) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> usersOptional = usersRepo.findById(id);
            if (usersOptional.isPresent()) {
                usersRepo.deleteById(id);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully deleted user with id " + id);
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User Not Found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Long userId, OurUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> usersOptional = usersRepo.findById(userId);
            if (usersOptional.isPresent()) {
                OurUsers existingUser = usersOptional.get();
                existingUser.setEnabled(updatedUser.isEnabled());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setRole(updatedUser.getRole());
                existingUser.setUsername(updatedUser.getUsername());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepo.save(existingUser);
                savedUser.setPassword(null);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully updated user with id " + userId);
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User Not Found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getMyInfo(String userName) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> usersOptional = usersRepo.findByUsername(userName);
            if (usersOptional.isPresent()) {
                OurUsers existingUser = usersOptional.get();
                existingUser.setPassword(null);
                reqRes.setOurUsers(existingUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully retrieved user info");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User Not Found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }
}
