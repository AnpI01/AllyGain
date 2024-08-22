package com.zenzsol.allygain.data.db;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.cloud.Timestamp;
import com.zenzsol.allygain.data.dto.db.Reservation;
import com.zenzsol.allygain.data.dto.db.Resource;

public interface ReservationDataHandler {
	public void addReservation(Reservation reservation) throws Exception;
	 public ArrayList<Reservation> getReservationsByTime(String cid, String rid,Timestamp dt) throws InterruptedException, ExecutionException;
	 public ArrayList<Reservation> getLastSixMonthsReservationsByUser(String uid) throws Exception;
	 public Map<String, Resource>  getResourcesMapIdKey(String cid) throws Exception;
	 public Reservation getReservation(String id) throws Exception;
	 public void deleteReservation(String id) throws Exception;
}
