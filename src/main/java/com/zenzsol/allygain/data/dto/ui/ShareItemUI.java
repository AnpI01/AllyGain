package com.zenzsol.allygain.data.dto.ui;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ShareItemUI {

	private boolean submitted;
	String id;
	@NotEmpty(message = "Name can't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid item name.")
	private String name;
    @Size(min = 0, max = 100, message = "Only 100 characters are allowed.")
	private String description;
	@NotEmpty(message = "Category can't be empty.")
    @Size(min = 2, max = 2, message = "Enter valid category.")
	private String category;
	private MultipartFile image;
	@Min(value = 6, message = "It should be between 06 to 21 hr")
    @Max(value = 21, message = "It should be between 06 to 21 hr")
	private int ptime;
	public boolean isSubmitted() {
		return submitted;
	}
	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public int getPtime() {
		return ptime;
	}
	public void setPtime(int ptime) {
		this.ptime = ptime;
	}
}
