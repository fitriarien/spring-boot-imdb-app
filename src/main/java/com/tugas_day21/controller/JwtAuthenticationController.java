package com.tugas_day21.controller;

import com.tugas_day21.model.UserDAO;
import com.tugas_day21.model.UserDetDAO;
import com.tugas_day21.model.UserDetDTO;
import com.tugas_day21.model.JwtRequest;
import com.tugas_day21.model.JwtResponse;
import com.tugas_day21.repository.UserRepo;
import com.tugas_day21.service.JwtUserDetailsService;
import com.tugas_day21.model.UserDTO;
import com.tugas_day21.config.JwtTokenUtil;
import com.tugas_day21.service.TokenBlacklistService;
import com.tugas_day21.util.CustomErrorType;
import com.tugas_day21.config.JwtBlacklistFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class JwtAuthenticationController {
	public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);
	@Autowired
	private PasswordEncoder bcryptEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JwtUserDetailsService userDetailsService;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private TokenBlacklistService tokenBlacklistService;
	@Autowired
	private JwtBlacklistFilter jwtBlacklistFilter;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		// check registered username
		final UserDAO user = userRepo.findByEmail(authenticationRequest.getEmail());
		if (user == null) {
			logger.error("Unable to login. Username of {} is not found.", authenticationRequest.getEmail());
			return new ResponseEntity<>(new CustomErrorType("Login Failed: We could not find your account."),
					HttpStatus.NOT_FOUND);
		}

		// check the password
		if (!(bcryptEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))) {
			logger.error("Unable to login. Password is wrong.");
			return new ResponseEntity<>(new CustomErrorType("Login Failed: Wrong Password. " +
					"Please re-enter the password."), HttpStatus.FORBIDDEN);
		}

		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		logger.info(userDetails.toString());

		// load user_id
		final long id = user.getId();
		// load token
		final JwtResponse jwtResponse = new JwtResponse(jwtTokenUtil.generateToken(userDetails));
		final String token = jwtResponse.getToken();
		// build token and id as response object
		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("id",id);

		return ResponseEntity.ok(response);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody Map<String, Object> payload) throws Exception {

		UserDTO userDTO = new UserDTO();
		userDTO.setEmail(payload.get("email").toString());
		userDTO.setPassword(payload.get("password").toString());
		userDTO.setRole("customer");

		logger.info("Step 1");
		boolean regexEmail = userDetailsService.emailRegex(userDTO.getEmail());
		boolean regexPassword = userDetailsService.passwordRegex(userDTO.getPassword());
		boolean isExist = userDetailsService.existUsername(userDTO.getEmail());

		logger.info("Step 2");
		UserDetDTO userDetDTO = new UserDetDTO();
		if (regexEmail && regexPassword) {
			if (!isExist) {
				logger.info("Step 3");

				@SuppressWarnings("unchecked")
				List<Map<String, Object>> userDet = (List<Map<String, Object>>) payload.get("user_detail");
				for (Map<String, Object> userDetObj : userDet) {
					logger.info("Adding Detail : {}", userDetObj.toString());

					userDetDTO.setName(userDetObj.get("name").toString());
					userDetDTO.setAddress(userDetObj.get("address").toString());
					userDetDTO.setGender(userDetObj.get("gender").toString());
					userDetDTO.setBirthDate(userDetObj.get("birthDate").toString());

					userDetailsService.addDetail(userDTO, userDetDTO);
				}

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Register success");
				return ResponseEntity.ok(response);
			} else {
				return new ResponseEntity<>(new CustomErrorType("Email has been registered. Please change the email or Login."),
						HttpStatus.NOT_ACCEPTABLE);
			}
		} else if (!regexEmail && regexPassword) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the email."),
					HttpStatus.NOT_ACCEPTABLE);
		} else if (regexEmail && !regexPassword) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the password."),
					HttpStatus.NOT_ACCEPTABLE);
		} else {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the email and password."),
					HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping(value = "/api/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) throws Exception {
		String token = jwtBlacklistFilter.extractToken(request);
		tokenBlacklistService.addToBlacklist(token);

		logger.info("Logout processing...");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Logout Successful!");
		return ResponseEntity.ok(response);
	}

	private void authenticate(String email, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}