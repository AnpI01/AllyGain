package com.zenzsol.allygain.data.dto.ui;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AgItemUI {
	private boolean submitted;
	@NotEmpty(message = "Name can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid item name.")
	private String name;
    @Size(min = 0, max = 100, message = "Only 100 characters are allowed.")
	private String description;
	@NotEmpty(message = "Category can't be empty.")
    @Size(min = 2, max = 30, message = "Enter valid category.")
	private String category;
	private MultipartFile image;
	private String price;
	private String uId;
	
	//used for modify case
	private String id;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}


