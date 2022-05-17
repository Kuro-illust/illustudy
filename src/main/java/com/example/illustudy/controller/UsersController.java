package com.example.illustudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;

import com.example.illustudy.entity.User;
import com.example.illustudy.entity.User.Authority;
import com.example.illustudy.form.UserForm;
import com.example.illustudy.repository.UserRepository;

@Controller
public class UsersController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;

	@GetMapping(path = "/users/new")
	public String newUser(Model model) {
		model.addAttribute("form", new UserForm());
		return "users/new";
	}
		
	 @RequestMapping(value = "/user", method = RequestMethod.POST)
	    public String create(@Validated @ModelAttribute("form") UserForm form, BindingResult result, Model model) {
	        String username = form.getUsername();
	        String email = form.getEmail();
	        String password = form.getPassword();
	        String passwordConfirmation = form.getPasswordConfirmation();
	
	        if (repository.findByEmail(email) != null) {
	            FieldError fieldError = new FieldError(result.getObjectName(), "email", "その E メールはすでに使用されています。");
	            result.addError(fieldError);
	        }
	        if (result.hasErrors()) {
	            return "users/new";
	        }
	        
	        User entity = new User(username, email, passwordEncoder.encode(password), Authority.ROLE_USER);
	        repository.saveAndFlush(entity);
	        
	        return "pages/index";
	 }
	
}
