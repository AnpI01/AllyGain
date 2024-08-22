package com.zenzsol.allygain.security;

import org.springframework.security.core.GrantedAuthority;

public enum AGUserRole implements GrantedAuthority{
	ROLE_AGADMIN (0),
	ROLE_USER (1);	

	private int rnum;

	AGUserRole(int rnum) {
		this.rnum = rnum;
	}

	public String getAuthority() {
		return toString();
	}

	public int getRnum() {
		return rnum;
	}

}
