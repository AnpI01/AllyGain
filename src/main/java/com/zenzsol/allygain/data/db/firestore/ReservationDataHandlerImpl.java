package com.zenzsol.allygain.data.db.firestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.zenzsol.allygain.data.db.ReservationDataHandler;
import com.zenzsol.allygain.data.dto.db.Reservation;
import com.zenzsol.allygain.data.dto.db.Resource;
import com.zenzsol.allygain.util.CommonUtils;
@Service
public class ReservationDataHandlerImpl implements ReservationDataHandler{
	
	private final Firestore db;
	
	private static final String RESOURCE_TBL = "resource";
	private static final String RESERVATION_TBL = "reservation";
	private static final String COUNTER_TBL = "counter";
	
	public ReservationDataHandlerImpl() {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		this.db = db;
	}
	
	@Override
	public void addReservation(Reservation reservation) throws Exception {
		if(reservation.getId().isEmpty()) {
		reservation.setId(getNextCounterForReservation().toString());
		}
		db.collection(RESERVATION_TBL).document(reservation.getId()).set(reservation);
		//db.collection(COMMUNITY_TBL).add(community);
	}
	private Integer getNextCounterForReservation() throws Exception {
		Map<String, Integer> idmap = new HashMap<String, Integer>();
		DocumentSnapshot doc = db.collection(COUNTER_TBL).document("forreservaton").get().get();

		Integer counter = 1001;
		if (doc.exists()) {			
			long tcr = (long) doc.get("ids");
			counter = Integer.valueOf((int)tcr);
		}
		counter = counter + 1;
		idmap.put("ids", counter);
		db.collection(COUNTER_TBL).document("forreservaton").set(idmap);

		return counter;
	}
	public Reservation getReservation(String id) throws Exception{	
		DocumentSnapshot docSnapshot  = db.collection(RESERVATION_TBL).document(id).get().get();
		return docSnapshot.toObject(Reservation.class);
	}
	public void deleteReservation(String id) throws Exception {
		db.collection(RESERVATION_TBL).document(id).delete();
	}
	public ArrayList<Reservation> getReservationsByTime(String cid, String rid, Timestamp dt) throws InterruptedException, ExecutionException  {

		Query query = db.collection(RESERVATION_TBL).whereEqualTo("cId", cid).whereEqualTo("rId", rid).whereEqualTo("dt", dt);
		
		QuerySnapshot querySnapshot = query.get().get();
		ArrayList<Reservation> resList =  new ArrayList<Reservation>();
		for (QueryDocumentSnapshot qs : querySnapshot) {
			resList.add(qs.toObject(Reservation.class));		
		}
		return resList;
	}
	public ArrayList<Reservation> getLastSixMonthsReservationsByUser(String uid) throws Exception {			

		Query query = db.collection(RESERVATION_TBL).whereEqualTo("uId", uid).whereGreaterThan("dt",
				Timestamp.of(CommonUtils.getSixMonthsBackDate()));

		QuerySnapshot querySnapshot = query.get().get();
		ArrayList<Reservation> itmList = new ArrayList<Reservation>();
		for (QueryDocumentSnapshot qs : querySnapshot) {			
			itmList.add(qs.toObject(Reservation.class));
		}
		return itmList;
	}
	public Map<String, Resource>  getResourcesMapIdKey(String cid) throws Exception {			

		Query  query = db.collection(RESOURCE_TBL).whereEqualTo("cId", cid); 

		QuerySnapshot querySnapshot = query.get().get();
		Map<String, Resource> resMap = new HashMap<String, Resource>();
		Resource res;
		for (QueryDocumentSnapshot qs : querySnapshot) {
			res = qs.toObject(Resource.class);
			resMap.put(res.getId() , res);
			
		}
		return resMap;
	}
}
