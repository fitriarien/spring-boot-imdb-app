package com.tugas_day21.controller;

import com.tugas_day21.model.UserDAO;
import com.tugas_day21.model.UserDetDAO;
import com.tugas_day21.model.UserDetDTO;
import com.tugas_day21.repository.UserDetRepo;
import com.tugas_day21.repository.UserRepo;
import com.tugas_day21.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class UserDetailRestController {
    public static final Logger logger = LoggerFactory.getLogger(UserDetailRestController.class);
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserDetRepo userDetRepo;

    // -------------------------- View User Profile -----------------------------------------
    @RequestMapping(value = "/user/{user_id}", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<?> viewProfile(@PathVariable("user_id") String user_id) throws SQLException, ClassNotFoundException {
        logger.info("Fetching Profile with id {}", user_id);

        UserDAO userDAO;
        UserDetDAO userDetDAO;
        try {
            long userIdLong = Long.parseLong(user_id);
            userDAO = userRepo.findById(userIdLong).orElse(null);
            userDetDAO = userDetRepo.findByUserDAO(userDAO);
        } catch (Exception e) {
            logger.error("Unable to view. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to view. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        if (userDAO == null || userDetDAO == null) {
            logger.error("User with id {} not found.", user_id);
            return new ResponseEntity<>(new CustomErrorType("User with id " + user_id  + " is not found"),
                    HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", userDetDAO.getUserDAO().getId());
        response.put("email", userDetDAO.getUserDAO().getEmail());
        response.put("role", userDetDAO.getUserDAO().getRole());
        response.put("name", userDetDAO.getName());
        response.put("address", userDetDAO.getAddress());
        response.put("gender", userDetDAO.getGender());
        response.put("birthDate", userDetDAO.getBirthDate());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // -------------------------- View User Profile -----------------------------------------
    @RequestMapping(value = "/user/view/{email}", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<?> viewProfileByEmail(@PathVariable("email") String email) throws SQLException, ClassNotFoundException {
        logger.info("Fetching Profile with email {}", email);

        UserDAO userDAO = userRepo.findByEmail(email);
        UserDetDAO userDetDAO = userDetRepo.findByUserDAO(userDAO);

        if (userDAO == null || userDetDAO == null) {
            logger.error("User with email {} not found.", email);
            return new ResponseEntity<>(new CustomErrorType("User with email " + email  + " is not found"),
                    HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", userDetDAO.getUserDAO().getId());
        response.put("email", userDetDAO.getUserDAO().getEmail());
        response.put("role", userDetDAO.getUserDAO().getRole());
        response.put("name", userDetDAO.getName());
        response.put("address", userDetDAO.getAddress());
        response.put("gender", userDetDAO.getGender());
        response.put("birthDate", userDetDAO.getBirthDate());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // -------------------------- Update Profile -----------------------------------------
    @RequestMapping(value = "/user/update/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProfile(@PathVariable("user_id") String user_id, @RequestBody UserDetDTO userDetDTO) throws SQLException, ClassNotFoundException {
        logger.info("Updating Profile with id {}", user_id);
        UserDAO currUserDAO;
        UserDetDAO currUser;
        try {
            long userIdLong = Long.parseLong(user_id);
            currUserDAO = userRepo.findById(userIdLong).orElse(null);
            currUser = userDetRepo.findById(userIdLong).orElse(null);
        } catch (Exception e) {
            logger.error("Unable to update. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to update. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        if (currUser == null) {
            logger.error("Unable to update. User with id {} not found.", user_id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. User with id " + user_id
                    + " is not found."), HttpStatus.NOT_FOUND);
        }

        // map data to DAO
        currUser.setName(userDetDTO.getName());
        currUser.setAddress(userDetDTO.getAddress());
        currUser.setGender(userDetDTO.getGender());
        currUser.setBirthDate(userDetDTO.getBirthDate());

        // save mapped data
        userDetRepo.save(currUser);
        return new ResponseEntity<>(currUser, HttpStatus.OK);
    }

}
