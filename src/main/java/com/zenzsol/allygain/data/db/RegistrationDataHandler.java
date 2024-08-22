package com.zenzsol.allygain.data.db;

import com.zenzsol.allygain.data.dto.db.Community;

public interface RegistrationDataHandler {

	public void registerCommunity(Community community) throws Exception;
	 public Community getCommunityBySPOCPhone(String phone) throws Exception;
	 public Community getCommunityByCommunityName(String name) throws Exception;
}
