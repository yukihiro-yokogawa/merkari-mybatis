package com.example.demo.form;

import java.util.function.Predicate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class MemberForm {
	
	@NotBlank(message="Please enter a mailAddress.")
	@Email(message="Incorrect format.")
	String mailAddress;
	
	@NotBlank(message="Please enter a password.")
	String password;
	
	@NotBlank(message="Please enter a confirmPassword.")
	String confirmPassword;
	
	@NotBlank(message="Please enter a onetimePassword.")
	@Pattern(regexp="[0-9]{6}",message="Incorrect format.")
	String onetimePassword;
	
	String authority;
	
	Predicate<Boolean> isPassCheck = s -> getPassword().equals(getConfirmPassword());
}
