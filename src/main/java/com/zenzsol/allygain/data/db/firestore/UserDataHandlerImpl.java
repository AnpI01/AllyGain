package com.zenzsol.allygain.data.db.firestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.zenzsol.allygain.data.db.UserDataHandler;
import com.zenzsol.allygain.data.dto.db.AgUser;
import com.zenzsol.allygain.data.dto.db.Message;
import com.zenzsol.allygain.data.dto.db.PasswdToken;
import com.zenzsol.allygain.data.dto.db.Resource;
import com.zenzsol.allygain.util.CommonUtils;

@Service
public class UserDataHandlerImpl implements UserDataHandler{

	private final Firestore db;

	private static final String P_TOKEN_TBL = "ptoken";
	private static final String USER_TBL = "user";
	private static final String RESOURCE_TBL = "resource";
	private static final String COUNTER_TBL = "counter";

	public UserDataHandlerImpl() {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		this.db = db;
	}

	private Integer getNextCounterForUser() throws Exception {
		Map<String, Integer> idmap = new HashMap<String, Integer>();
		DocumentSnapshot doc = db.collection(COUNTER_TBL).document("foruser").get().get();

		Integer counter = 1001;
		if (doc.exists()) {			
			long tcr = (long) doc.get("ids");
			counter = Integer.valueOf((int)tcr);
		}
		counter = counter + 1;
		idmap.put("ids", counter);
		db.collection(COUNTER_TBL).document("foruser").set(idmap);

		return counter;
	}
	private Integer getNextCounterForResource() throws Exception {
		Map<String, Integer> idmap = new HashMap<String, Integer>();
		DocumentSnapshot doc = db.collection(COUNTER_TBL).document("forresource").get().get();

		Integer counter = 1001;
		if (doc.exists()) {			
			long tcr = (long) doc.get("ids");
			counter = Integer.valueOf((int)tcr);
		}
		counter = counter + 1;
		idmap.put("ids", counter);
		db.collection(COUNTER_TBL).document("forresource").set(idmap);

		return counter;
	}

	@Override
	public void registerAgUser(AgUser user) throws Exception {
		//set uid
		user.setId(getNextCounterForUser().toString());
		db.collection(USER_TBL).document(user.getEmail().toLowerCase()).set(user);
	}
	@Override
	public void updateAgUser(AgUser user) throws Exception {
		db.collection(USER_TBL).document(user.getEmail().toLowerCase()).set(user);
	}
	@Override
	public PasswdToken generateAndStorePasswdToken(String email) throws Exception {
		String passtoken = UUID.randomUUID().toString();
		PasswdToken ptoken = new PasswdToken();
		ptoken.setMail(email);
		ptoken.setToken(passtoken);
		ptoken.setCdt(Timestamp.of(CommonUtils.getDateWithoutTime((new Date()))));
		
		db.collection(P_TOKEN_TBL).document(ptoken.getMail()).set(ptoken);
		//log.info("p reset token "+passtoken);
		return ptoken;
	}
	@Override
	public PasswdToken getPasswordToken(String token) throws Exception{	
		Query query  = db.collection(P_TOKEN_TBL).whereEqualTo("token", token); 
		ApiFuture<QuerySnapshot> queryS = query.get();

		List<QueryDocumentSnapshot> querySnapshot = queryS.get().getDocuments();
		return querySnapshot.get(0).toObject(PasswdToken.class);		
		
	}
	@Override
	public void addResource(Resource resource) throws Exception {
		if(resource.getId().isEmpty()) {
			resource.setId(getNextCounterForResource().toString());
		}
		db.collection(RESOURCE_TBL).document(resource.getId()).set(resource);
		//db.collection(COMMUNITY_TBL).add(community);
	}
	public void deleteResource(String resId) throws Exception {
		db.collection(RESOURCE_TBL).document(resId).delete();
	}
	public Resource getResourceById(String id) throws Exception{	
		DocumentSnapshot docSnapshot  = db.collection(RESOURCE_TBL).document(id).get().get();
		return docSnapshot.toObject(Resource.class);
	}

	public ArrayList<Resource> getResourcesByCommunity(String communityId) throws Exception{	
		Query  query = db.collection(RESOURCE_TBL).whereEqualTo("cId", communityId); 

		ApiFuture<QuerySnapshot> queryS = query.get();

		QuerySnapshot querySnapshot = queryS.get();
		ArrayList<Resource> itemList =  new ArrayList<Resource>();
		for (QueryDocumentSnapshot qs : querySnapshot) {
			itemList.add(qs.toObject(Resource.class));		
		}
		return itemList;
	}

	@Override
	public Resource getResource(String resId) throws Exception {
		return db.collection(RESOURCE_TBL).document(resId).get().get().toObject(Resource.class);
	}
	public AgUser getUserByUsername(String username) throws Exception{	
		DocumentSnapshot docSnapshot  = db.collection(USER_TBL).document(username.toLowerCase()).get().get();
		
		//log.info("user doc #### "+docSnapshot.toString());
		return docSnapshot.toObject(AgUser.class);
	}
	public AgUser getUserById(String id) throws Exception{
		Query  query = db.collection(USER_TBL).whereEqualTo("id", id); 

		ApiFuture<QuerySnapshot> queryS = query.get();

		QuerySnapshot querySnapshot = queryS.get();		
		
		for (QueryDocumentSnapshot qs : querySnapshot) {
			return qs.toObject(AgUser.class);		
		}
		return null;

	}
	 public void addMessage(Message msg)  throws Exception{
		 Map<String, String> fields = new HashMap<String, String>();
		 fields.put("name", msg.getName());
		 fields.put("message", msg.getMessage());		 
		 
		 db.collection("MSG").document(msg.getEmail().toLowerCase()).set(fields);
	 }
}
