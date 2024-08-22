package com.zenzsol.allygain.data.dto.ui;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CommunityUI {
	boolean submitted;
	
	@NotEmpty(message = "Name of the community can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid community name.")
	private String name;

	@NotEmpty(message = "community address can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid community address.")
	private String addressOne;
	
    @Size( max = 100, message = "Enter valid community address .")
    private String addressTwo;
	
	@NotEmpty(message = "City can't be empty.")
    @Size(min = 5, max = 100, message = "Enter city.")
	private String city;
	
	@NotEmpty(message = "State can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid state name.")
	private String state;
	
	@NotEmpty(message = "Country can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid country name.")
	private String country;
	
	@NotEmpty(message = "SPOC name can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid spoc name.")
	private String spoc;	
	
	@NotEmpty(message = "SPOC contact number can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid spoc contact number.")
	private String phone;
	
	@NotEmpty(message = "Spoc email cann't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid spoc email id.")
	@Email
	private String email;

	@NotEmpty(message = "Passwprd cann't be empty.")
    @Size(min = 8, max = 100, message = "Password should be min 8 characters")
	String password;	

	
	public boolean isSubmitted() {
		return submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddressOne() {
		return addressOne;
	}
	public void setAddressOne(String addressOne) {
		this.addressOne = addressOne;
	}
	public String getAddressTwo() {
		return addressTwo;
	}
	public void setAddressTwo(String addressTwo) {
		this.addressTwo = addressTwo;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSpoc() {
		return spoc;
	}
	public void setSpoc(String spoc) {
		this.spoc = spoc;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	
}
