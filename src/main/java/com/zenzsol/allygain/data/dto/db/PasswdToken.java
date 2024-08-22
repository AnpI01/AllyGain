package com.zenzsol.allygain.data.dto.db;

import com.google.cloud.Timestamp;

public class PasswdToken {
	private String mail;
	private String token;
	private Timestamp cdt;
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Timestamp getCdt() {
		return cdt;
	}
	public void setCdt(Timestamp cdt) {
		this.cdt = cdt;
	}	
	
}
