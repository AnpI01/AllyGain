package com.zenzsol.allygain.data.db;

import java.util.ArrayList;

import com.zenzsol.allygain.data.dto.db.Item;
import com.zenzsol.allygain.data.dto.db.ShareItem;

public interface TradeItemsDataHandler {

	public Integer getNextCounterForFile() throws Exception;
	
	public ArrayList<Item> getItemsOrderByCat(String cat, String lastItem) throws Exception;
	
	public ArrayList<Item> getItemsStartingFromLatest(String lastItem) throws Exception;
	
	public Integer getNextCounterForItem() throws Exception;
	
	public void addTradeItem(Item item) throws Exception;
	
	public Item getTradeItem(String itemid) throws Exception;
	
	public ArrayList<Item> getLastSixMonthsItemsPostedByUser(String uid) throws Exception;
	public void addShareItem(ShareItem item) throws Exception;
	public ArrayList<ShareItem> getLastSixMonthsLitedSharingItemsByUser(String uid) throws Exception;
	public ArrayList<ShareItem> getSharingItemsStartingFromLatest(String lastItem, String uid) throws Exception;
	public ArrayList<ShareItem> getSharingItemsOrderByCat(String cat, String lastItem, String uid) throws Exception;
	public void deleteTradeItem(String itemId) throws Exception;
	public void deleteShareItem(String itemId) throws Exception;
	public ShareItem getShareItem(String itemid) throws Exception;
	public void updateShareItemBorrowStatus(String itemId, String status) throws Exception;
	public ArrayList<ShareItem> getSharedItemsBorrowedByOtherUsers(String uid) throws Exception;
	public ArrayList<ShareItem> getOthersSharedItemsBorrowedByUser(String uid) throws Exception;
}
