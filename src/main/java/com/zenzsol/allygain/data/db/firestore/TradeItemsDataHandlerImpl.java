package com.zenzsol.allygain.data.db.firestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.zenzsol.allygain.data.db.TradeItemsDataHandler;
import com.zenzsol.allygain.data.dto.db.Item;
import com.zenzsol.allygain.data.dto.db.ShareItem;
import com.zenzsol.allygain.util.CommonUtils;

@Service
public class TradeItemsDataHandlerImpl implements TradeItemsDataHandler {

	private static final Logger log = Logger.getLogger(TradeItemsDataHandlerImpl.class.getName());
	private final Firestore db;

	private static final String ITEM_TBL = "items";
	private static final String SHARE_ITEM_TBL = "shareitems";
	private static final String COUNTER_TBL = "counter";

	public TradeItemsDataHandlerImpl() {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		this.db = db;
	}

	@Override
	public Integer getNextCounterForFile() throws Exception {
		Map<String, Integer> idmap = new HashMap<String, Integer>();
		DocumentSnapshot doc = db.collection(COUNTER_TBL).document("forimg").get().get();

		Integer counter = 2001;
		if (doc.exists()) {
			long tcr = (long) doc.get("ids");
			counter = Integer.valueOf((int) tcr);
		}
		counter = counter + 1;
		// type 1 for image file counter
		idmap.put("ids", counter);
		db.collection(COUNTER_TBL).document("forimg").set(idmap);

		return counter;
	}

	@Override
	public Integer getNextCounterForItem() throws Exception {
		Map<String, Integer> idmap = new HashMap<String, Integer>();
		DocumentSnapshot doc = db.collection(COUNTER_TBL).document("foritem").get().get();

		Integer counter = 2001;
		if (doc.exists()) {
			long tcr = (long) doc.get("ids");
			counter = Integer.valueOf((int) tcr);
		}
		counter = counter + 1;
		// type 2 for item id counter
		idmap.put("ids", counter);
		db.collection(COUNTER_TBL).document("foritem").set(idmap);

		return counter;
	}

	@Override
	public void addTradeItem(Item item) throws Exception {
		//new item or modify
		if(item.getId().isEmpty()) {
		item.setId(getNextCounterForItem().toString());
		}				
		db.collection(ITEM_TBL).document(item.getId()).set(item);
	}

	public void deleteTradeItem(String itemId) throws Exception {
		db.collection(ITEM_TBL).document(itemId).delete();
	}
	@Override
	public Item getTradeItem(String itemid) throws Exception {

		return db.collection(ITEM_TBL).document(itemid).get().get().toObject(Item.class);
	}

	public ArrayList<Item> getItemsStartingFromLatest(String lastItem) throws Exception {
		Query query;
		if (!lastItem.isEmpty()) {
			query = db.collection(ITEM_TBL).orderBy(FieldPath.documentId()).startAfter(lastItem).limit(15);

		} else {
			query = db.collection(ITEM_TBL).orderBy(FieldPath.documentId()).limit(15);
		}

		return processItemsQuery(query);
	}

	public ArrayList<Item> getItemsOrderByCat(String cat, String lastItem) throws Exception {
		Query query;
		if (!lastItem.isEmpty()) {
			log.info("lastItem data access " + lastItem);
			query = db.collection(ITEM_TBL).whereEqualTo("cat", cat).orderBy(FieldPath.documentId())
					.startAfter(lastItem).limit(15);

		} else {
			query = db.collection(ITEM_TBL).whereEqualTo("cat", cat).orderBy(FieldPath.documentId()).limit(15);
		}

		return processItemsQuery(query);
	}

	private ArrayList<Item> processItemsQuery(Query query) throws Exception {
		ApiFuture<QuerySnapshot> queryS = query.get();

		QuerySnapshot querySnapshot = queryS.get();
		ArrayList<Item> itemList = new ArrayList<Item>();
		for (QueryDocumentSnapshot qs : querySnapshot) {
			itemList.add(qs.toObject(Item.class));
		}
		//log.info("items size from db " + itemList.size());
		return itemList;
	}

	public ArrayList<Item> getLastSixMonthsItemsPostedByUser(String uid) throws Exception {			

		Query query = db.collection(ITEM_TBL).whereEqualTo("uId", uid).whereGreaterThan("cDt",
				Timestamp.of(CommonUtils.getSixMonthsBackDate()));

		QuerySnapshot querySnapshot = query.get().get();
		ArrayList<Item> itmList = new ArrayList<Item>();
		for (QueryDocumentSnapshot qs : querySnapshot) {
			itmList.add(qs.toObject(Item.class));
		}
		return itmList;
	}

	private Integer getNextCounterForShareItem() throws Exception {
		Map<String, Integer> idmap = new HashMap<String, Integer>();

		DocumentSnapshot doc = db.collection(COUNTER_TBL).document("forshareitem").get().get();

		Integer counter = 1101;
		if (doc.exists()) {
			long tcr = (long) doc.get("ids");
			counter = Integer.valueOf((int) tcr);
		}
		
		counter = counter + 1;
		// type 2 for item id counter
		idmap.put("ids", counter);
		db.collection(COUNTER_TBL).document("forshareitem").set(idmap);

		return counter;
	}
	@Override
	public void addShareItem(ShareItem item) throws Exception {
		if(item.getId().isEmpty()) {
			item.setId(getNextCounterForShareItem().toString());
		}
		db.collection(SHARE_ITEM_TBL).document(item.getId()).set(item);
	}
	@Override
	public void updateShareItemBorrowStatus(String itemId, String buid) throws Exception {
		db.collection(SHARE_ITEM_TBL).document(itemId).update("bUid",buid);
	}

	public void deleteShareItem(String itemId) throws Exception {
		db.collection(SHARE_ITEM_TBL).document(itemId).delete();
	}
	public ArrayList<ShareItem> getLastSixMonthsLitedSharingItemsByUser(String uid) throws Exception {			

		Query query = db.collection(SHARE_ITEM_TBL).whereEqualTo("uId", uid).whereGreaterThanOrEqualTo("cDt",
				Timestamp.of(CommonUtils.getSixMonthsBackDate()));

		return processSharingItemsQuery(query);
	}
	public ArrayList<ShareItem> getSharedItemsBorrowedByOtherUsers(String uid) throws Exception {			

		Query query = db.collection(SHARE_ITEM_TBL).whereEqualTo("uId", uid).whereNotEqualTo("bUid", "");

		return processSharingItemsQuery(query);
	}
	public ArrayList<ShareItem> getOthersSharedItemsBorrowedByUser(String uid) throws Exception {			

		Query query = db.collection(SHARE_ITEM_TBL).whereEqualTo("bUid", uid);

		return processSharingItemsQuery(query);
	}
	public ArrayList<ShareItem> getSharingItemsStartingFromLatest(String lastItem, String uid) throws Exception {
		Query query;
		if (!lastItem.isEmpty()) {
			query = db.collection(SHARE_ITEM_TBL).whereEqualTo("bUid", "").orderBy(FieldPath.documentId()).startAfter(lastItem).limit(15);

		} else {
			query = db.collection(SHARE_ITEM_TBL).whereEqualTo("bUid", "").orderBy(FieldPath.documentId()).limit(15);
		}

		return processSharingItemsQueryWithFilter(query, uid);
	}

	public ArrayList<ShareItem> getSharingItemsOrderByCat(String cat, String lastItem, String uid) throws Exception {
		Query query;
		if (!lastItem.isEmpty()) {
			query = db.collection(SHARE_ITEM_TBL).whereEqualTo("bUid", "").whereEqualTo("cat", cat).orderBy(FieldPath.documentId())
					.startAfter(lastItem).limit(15);

		} else {
			query = db.collection(SHARE_ITEM_TBL).whereEqualTo("bUid", "").whereEqualTo("cat", cat).orderBy(FieldPath.documentId()).limit(15);
		}

		return processSharingItemsQueryWithFilter(query, uid);
	}
	//filter share items posted by the user
	private ArrayList<ShareItem> processSharingItemsQueryWithFilter(Query query, String uid) throws Exception {
		ApiFuture<QuerySnapshot> queryS = query.get();

		QuerySnapshot querySnapshot = queryS.get();
		ArrayList<ShareItem> itemList = new ArrayList<ShareItem>();
		ShareItem si;
		for (QueryDocumentSnapshot qs : querySnapshot) {
			si = qs.toObject(ShareItem.class);
			if(si.getuId().equals(uid)) {
				continue;
			}
			itemList.add(si);
		}
		return itemList;
	}
	private ArrayList<ShareItem> processSharingItemsQuery(Query query) throws Exception {
		ApiFuture<QuerySnapshot> queryS = query.get();

		QuerySnapshot querySnapshot = queryS.get();
		ArrayList<ShareItem> itemList = new ArrayList<ShareItem>();
		for (QueryDocumentSnapshot qs : querySnapshot) {
			itemList.add(qs.toObject(ShareItem.class));
		}
		return itemList;
	}
	/**
	 * Get share items details for borrowing
	 */
	@Override
	public ShareItem getShareItem(String itemid) throws Exception {
		return db.collection(SHARE_ITEM_TBL).document(itemid).get().get().toObject(ShareItem.class);
	}

}
