package com.zenzsol.allygain.data.db;

import java.util.ArrayList;

import com.zenzsol.allygain.data.dto.db.AgUser;
import com.zenzsol.allygain.data.dto.db.Message;
import com.zenzsol.allygain.data.dto.db.PasswdToken;
import com.zenzsol.allygain.data.dto.db.Resource;

public interface UserDataHandler {
	public void registerAgUser(AgUser user) throws Exception;
	public void updateAgUser(AgUser user) throws Exception;
	public void addResource(Resource resource) throws Exception;
	 public AgUser getUserByUsername(String username) throws Exception;
	 public ArrayList<Resource> getResourcesByCommunity(String communityId) throws Exception;
	 public Resource getResourceById(String id) throws Exception;
	 public Resource getResource(String resId) throws Exception;
	 public void deleteResource(String resId) throws Exception;
	 public AgUser getUserById(String id) throws Exception;
	 public PasswdToken generateAndStorePasswdToken(String email) throws Exception;
	 public PasswdToken getPasswordToken(String token) throws Exception;
	 public void addMessage(Message msg)  throws Exception;
}
