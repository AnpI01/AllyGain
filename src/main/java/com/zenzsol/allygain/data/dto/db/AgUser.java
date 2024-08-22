package com.zenzsol.allygain.data.dto.db;

import com.zenzsol.allygain.security.AGUserRole;

public class AgUser {
	private String id;
	private String email;
	private String password;	

	private String unitId;
	private AGUserRole authorities;
	
	private String communityId;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getUsername() {
		return unitId;
	}
	public void setUsername(String username) {
		this.unitId = username;
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
	public AGUserRole getAuthorities() {
		return authorities;
	}
	public void setAuthorities(AGUserRole authorities) {
		this.authorities = authorities;
	}
	public String getCommunityId() {
		return communityId;
	}
	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}	
	
}
