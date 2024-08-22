package com.zenzsol.allygain.security;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.zenzsol.allygain.data.db.UserDataHandler;
import com.zenzsol.allygain.data.dto.db.AgUser;

public class AGUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDataHandler userDataHandler;
	private static final Logger log = Logger.getLogger(AGUserDetailsService.class.getName());

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username != null) {
			username = username.toLowerCase();
		}
		AgUser user = null;
		try {
			user = userDataHandler.getUserByUsername(username);
		} catch (Exception e) {
			log.info("load user failed.");
		}
		if (user == null || user.getPassword() == null) {
			throw new UsernameNotFoundException("User not found");
		}
		Set<AGUserRole> roles = EnumSet.noneOf(AGUserRole.class);
		roles.add(user.getAuthorities());
		
		AGUserDetails userdetails = new AGUserDetails(roles, user.getPassword(), user.getEmail(), true,
				true, true, true);
		userdetails.setCommunityId(user.getCommunityId());
		userdetails.setuId(user.getId());
		userdetails.setResUnit(user.getUnitId());
		return userdetails;
	}
}
