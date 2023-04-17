package com.tugas_day21.service;

import com.tugas_day21.model.UserDTO;
import com.tugas_day21.model.UserDAO;
import com.tugas_day21.model.UserDetDAO;
import com.tugas_day21.model.UserDetDTO;
import com.tugas_day21.repository.UserDetRepo;
import com.tugas_day21.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	public static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserDetRepo userDetRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Loading User : {}", username);
		UserDAO user = userRepo.findByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
	}

	public UserDAO save(UserDTO user) {
		UserDAO newUser = new UserDAO();
		newUser.setEmail(user.getEmail());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setRole(user.getRole());

		return userRepo.save(newUser);
	}

	public UserDetDAO addDetail(UserDTO user, UserDetDTO userDet) {
		UserDAO newUser = new UserDAO();
		newUser.setEmail(user.getEmail());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setRole(user.getRole());

		UserDetDAO newUserDet = new UserDetDAO();
		newUserDet.setName(userDet.getName());
		newUserDet.setAddress(userDet.getAddress());
		newUserDet.setGender(userDet.getGender());
		newUserDet.setBirthDate(userDet.getBirthDate());
		newUserDet.setUserDAO(newUser);

		return userDetRepo.save(newUserDet);
	}

	public boolean passwordRegex(String password) {
		boolean regexPassword;
		boolean pass_regex = Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
				password);
		if (pass_regex) {
			regexPassword = true;
		} else {
			regexPassword = false;
		}
		return regexPassword;
	}

	public boolean emailRegex(String email) {
		boolean regexEmail;
		boolean email_regex = Pattern.matches("([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})",
				email);
		if (email_regex) {
			regexEmail = true;
		} else {
			regexEmail = false;
		}
		return regexEmail;
	}

	public boolean existUsername(String username) {
		boolean isExist = false;
		List<UserDAO> users = (List<UserDAO>) userRepo.findAll();
		for (UserDAO curr: users) {
			if (username.equals(curr.getEmail())) {
				isExist = true;
				break;
			} else {
				isExist = false;
			}
		}
		return isExist;
	}
}