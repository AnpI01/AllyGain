package com.zenzsol.allygain.data.dto.ui;

import org.springframework.web.multipart.MultipartFile;

public class UsersFile {
	private boolean submitted;

	private MultipartFile users;

	public boolean isSubmitted() {
		return submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}

	public MultipartFile getUsers() {
		return users;
	}

	public void setUsers(MultipartFile users) {
		this.users = users;
	}	
	
}
