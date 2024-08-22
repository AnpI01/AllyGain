package com.zenzsol.allygain.data.db.firestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.zenzsol.allygain.data.db.RegistrationDataHandler;
import com.zenzsol.allygain.data.dto.db.Community;

@Service
public class RegistrationDataHandlerImpl implements RegistrationDataHandler{

	 private final Firestore db;
	 
	 private static final String COMMUNITY_TBL = "community";
	 private static final String COUNTER_TBL = "counter";
	 
	 public RegistrationDataHandlerImpl() {
			Firestore db = FirestoreOptions.getDefaultInstance().getService();
			this.db = db;
	 }
	
		
		private Integer getNextCounterForCommunity() throws Exception {
			Map<String, Integer> idmap = new HashMap<String, Integer>();
			DocumentSnapshot doc = db.collection(COUNTER_TBL).document("forcommunity").get().get();
			
			Integer counter = 1001;
			if (doc.exists()) {			
				long tcr = (long) doc.get("ids");
				counter = Integer.valueOf((int)tcr);
			}
			counter = counter + 1;
			idmap.put("ids", counter);
			db.collection(COUNTER_TBL).document("forcommunity").set(idmap);

			return counter;
		}

	@Override
	public void registerCommunity(Community community) throws Exception {
		community.setId(getNextCounterForCommunity().toString());
		db.collection(COMMUNITY_TBL).document(community.getId()).set(community);
	}
	
	 
	 private Community processCommunityQuery(Query query) throws Exception{
		 ApiFuture<QuerySnapshot> queryS = query.get();
		 
			List<QueryDocumentSnapshot> querySnapshot = queryS.get().getDocuments();
			if(querySnapshot.size()>=1)
				return querySnapshot.get(0).toObject(Community.class);	
			else
				return null;

	 }
	 
	 public Community getCommunityBySPOCPhone(String phone) throws Exception{	
		 Query query = db.collection(COMMUNITY_TBL).whereEqualTo("phone", phone);

		 return processCommunityQuery(query);
	 }
	 
	 public Community getCommunityByCommunityName(String name) throws Exception{	
		 Query query = db.collection(COMMUNITY_TBL).whereEqualTo("name", name);

		 return processCommunityQuery(query);
	 }
}
