package com.zenzsol.allygain.security;

import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

public class AGUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private String communityId;
	private String uId;
	private Set<AGUserRole> authorities;
	private  String password;
	private  String username;
	private String resUnit;
	private  boolean accountNonExpired;
	private  boolean accountNonLocked;
	private  boolean credentialsNonExpired;
	private boolean enabled;
	
	public AGUserDetails(){}
	
	public AGUserDetails(Set<AGUserRole> authorities,String password,String username, 
			boolean ane, boolean anl, boolean cne, boolean enabled){
		this.authorities = authorities;
		this.password = password;
		this.username = username;
		this.accountNonExpired = ane;
		this.accountNonLocked = anl;
		this.credentialsNonExpired =  cne;
		this.enabled =  enabled;		
	}
	
	public Set<AGUserRole> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<AGUserRole> authorities) {
		this.authorities = authorities;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCommunityId() {
		return communityId;
	}

	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getResUnit() {
		return resUnit;
	}

	public void setResUnit(String resUnit) {
		this.resUnit = resUnit;
	}

}
