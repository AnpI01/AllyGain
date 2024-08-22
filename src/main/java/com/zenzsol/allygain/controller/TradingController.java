package com.zenzsol.allygain.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.cloud.Timestamp;
import com.zenzsol.allygain.data.db.TradeItemsDataHandler;
import com.zenzsol.allygain.data.db.UserDataHandler;
import com.zenzsol.allygain.data.db.cloudstore.AgCloudStoreDataHandler;
import com.zenzsol.allygain.data.dto.db.AgUser;
import com.zenzsol.allygain.data.dto.db.Item;
import com.zenzsol.allygain.data.dto.db.ShareItem;
import com.zenzsol.allygain.data.dto.ui.AgItemUI;
import com.zenzsol.allygain.data.dto.ui.ShareItemUI;
import com.zenzsol.allygain.email.EmailManager;
import com.zenzsol.allygain.security.AGUserDetails;
import com.zenzsol.allygain.util.CommonUtils;

import jakarta.validation.Valid;
@Controller
public class TradingController {
	private static final Logger log = Logger.getLogger(TradingController.class.getName());
	@Autowired
	private TradeItemsDataHandler itemDataHandler;
	@Autowired
	private UserDataHandler userDataHandler;
	@Autowired
	private AgCloudStoreDataHandler cloudStore;
	@Autowired
	private EmailManager emailManager;
	
	@GetMapping("/trade/item/add")
	public String addItemsPage(ModelMap model) {
		model.put("agItemUI", new AgItemUI());		
		return "/ag/content/trade/additem";
	}


	@PostMapping("/trade/item/add")
	public String addItems(@Valid AgItemUI agItemUI, BindingResult result) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		if (!result.hasErrors()) {
			agItemUI.setuId(activeUser.getuId());              
			try {
				String imagePath = "";
				if(!agItemUI.getImage().isEmpty()) {				
				   imagePath = cloudStore.uploadImageFile(agItemUI.getImage().getContentType(), "buysell",
						agItemUI.getCategory(), agItemUI.getImage().getBytes()); 				
				}
				Item item = createDBItemObj(agItemUI, activeUser.getCommunityId());
				item.setImg(imagePath);
				itemDataHandler.addTradeItem(item);
			} catch (Exception e) {			
				log.info("addCommunity post upload image error "+e);
				result.addError(new ObjectError("message", "Failed to save the item info."));
				return "/ag/content/trade/additem";
			}
			agItemUI.setSubmitted(true);
		}
		return "/ag/content/trade/additem";
	}
	
	@GetMapping("/trade/myitems/view")
	public String viewPostedItemsByUser(ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		ArrayList<Item> itmslst = new ArrayList<Item>();
		try {
			itmslst = itemDataHandler.getLastSixMonthsItemsPostedByUser(activeUser.getuId());
		} catch (Exception e) {
			log.info("viewPostedItemsByUser error "+e);
		} 
		model.put("itemslist", itmslst);		
		return "/ag/content/trade/viewmyitems";
	}
	
	
	@GetMapping("/trade/items/{category}")
	public String displayItems(@PathVariable String category, ModelMap model) {
				
		ArrayList<Item> itemsLst = new ArrayList<Item>();
		try {
			if("all".equals(category)) {				
				itemsLst = itemDataHandler.getItemsStartingFromLatest("");
			}else {
				itemsLst = itemDataHandler.getItemsOrderByCat(category, "");
			}
			
		} catch (Exception e) {
			log.info("displayItems error "+e);
		}
		if(itemsLst.size() < 15) {
			model.put("lstitem", "end");
		}else {
			model.put("lstitem", itemsLst.get(itemsLst.size() - 1).getId());
		}
		model.put("category", category);	
		 model.put("itemslist", itemsLst);
		return "/ag/content/trade/listitems";
	}
	
	@PostMapping("/trade/items/nextitems")
	public String displayNextSetItems(ModelMap model, @RequestParam("catgry") String category, @RequestParam("curval") String lastitem) {	
		ArrayList<Item> itemsLst = new ArrayList<Item>();

		try {
			if("all".equals(category)) {
				itemsLst = itemDataHandler.getItemsStartingFromLatest(lastitem);
			}else {
				itemsLst = itemDataHandler.getItemsOrderByCat(category, lastitem);
			}
			
		} catch (Exception e) {
			log.info("displayItems error "+e);
		}
		if(itemsLst.size() < 15) {
			model.put("lstitem", "end");
		}else {
			model.put("lstitem", itemsLst.get(itemsLst.size() - 1).getId());
		}
		
		 
		 model.put("itemslist", itemsLst);
		return "/ag/content/trade/ajaxitems";
	}
	
	@GetMapping("/trade/item/modify/{itemid}")
	public String modifyItem(@PathVariable String itemid, ModelMap model) {
		AgItemUI agItemUI = new AgItemUI();
		try {
			Item agitem = itemDataHandler.getTradeItem(itemid);
			agItemUI = getAgItemUi(agitem);
		} catch (Exception e) {
			agItemUI = new AgItemUI();
			log.info("viewItem error "+e);
		}
		model.put("agItemUI", agItemUI);	
		return "/ag/content/trade/additem";
	}
	private AgItemUI getAgItemUi(Item agitem) {
		AgItemUI agitemui = new AgItemUI();
		agitemui.setId(agitem.getId());
		agitemui.setCategory(agitem.getCat());
		agitemui.setDescription(agitem.getDesc());
		agitemui.setName(agitem.getName());
		agitemui.setPrice(agitem.getPrice());
		return agitemui;
	}
	@GetMapping("/trade/item/delete/{itemid}")
	public String deleteItem(@PathVariable String itemid, ModelMap model) {
		
		model.put("itemid", itemid);
		model.put("deleted", "n");
		return "/ag/content/trade/deleteitem";
	}
	@GetMapping("/trade/item/delete/{answ}/{itemid}")
	public String deleteItemYesNo(@PathVariable String answ, @PathVariable String itemid, ModelMap model) {
		if(answ.equals("accept")) {
			try {
				itemDataHandler.deleteTradeItem(itemid);
			} catch (Exception e) {				
				log.info("deleteItem error "+e);
				return "redirect:/trade/myitems/view";
			}
			model.put("deleted", "y");
			return "/ag/content/trade/deleteitem";
		}		
		
		return "redirect:/trade/myitems/view";
	}
	@GetMapping("/trade/item/view/{itemid}")
	public String viewItem(@PathVariable String itemid, ModelMap model) {
		
		Item agitem;
		AgUser aguser;
		try {
			agitem = itemDataHandler.getTradeItem(itemid);
			aguser = userDataHandler.getUserById(agitem.getuId());
		} catch (Exception e) {
			agitem = new Item();
			aguser = new AgUser();
			log.info("viewItem error "+e);
		}
		
		model.put("item", agitem);
		model.put("aguser", aguser);
		
		return "ag/content/trade/viewitem";
	}
	
	private Item createDBItemObj(AgItemUI itemUI, String cid) {
		Item item = new Item();
		item.setId(itemUI.getId());
		item.setCat(itemUI.getCategory());
		item.setDesc(itemUI.getDescription());
		item.setName(itemUI.getName());
		item.setPrice(itemUI.getPrice());
		item.setcId(cid);
		item.setuId(itemUI.getuId());
		item.setcDt(Timestamp.of(CommonUtils.getDateWithoutTime((new Date()))));
		return item;
	}
	
	@GetMapping("/share/item/add")
	public String addShareItem(ModelMap model) {
		model.put("shareItemUI", new ShareItemUI());		
		return "/ag/content/share/addshareitem";
	}


	@PostMapping("/share/item/add")
	public String addShareItem(@Valid ShareItemUI shareItemUI, BindingResult result) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		if (!result.hasErrors()) {		         
			try {
				String imagePath = "";
				if(!shareItemUI.getImage().isEmpty()) {
				 imagePath = cloudStore.uploadImageFile(shareItemUI.getImage().getContentType(), "sharing",
						shareItemUI.getCategory(), shareItemUI.getImage().getBytes());   
				}
				ShareItem item = createDBShareItemObj(shareItemUI, activeUser.getCommunityId(), activeUser.getuId());
				item.setImage(imagePath);
				itemDataHandler.addShareItem(item);
			} catch (Exception e) {
				log.info("Share item error "+e);
				result.addError(new ObjectError("message", "Failed to save the item info."));
				return "/ag/content/share/addshareitem";
			}
			shareItemUI.setSubmitted(true);
		}
		return "/ag/content/share/addshareitem";
	}
	private ShareItem createDBShareItemObj(ShareItemUI itemUI, String cid, String uid) {
		ShareItem item = new ShareItem();
		item.setId(itemUI.getId());
		item.setDesc(itemUI.getDescription());
		item.setName(itemUI.getName());
		item.setPtime(itemUI.getPtime());
		item.setCat(itemUI.getCategory());
		item.setcId(cid);
		item.setuId(uid);
		item.setcDt(Timestamp.of(CommonUtils.getDateWithoutTime((new Date()))));
		return item;
	}
	
	@GetMapping("/share/myitems/view")
	public String viewSharingItemsPostedByUser(ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		ArrayList<ShareItem> itmslst = new ArrayList<ShareItem>();
		try {
			itmslst = itemDataHandler.getLastSixMonthsLitedSharingItemsByUser(activeUser.getuId());
		} catch (Exception e) {
			log.info("viewPostedItemsByUser error "+e);
		} 
		model.put("itemslist", itmslst);
		 model.put("catmap", getSharingCategoryMap());
		 model.put("itmlnk", "My Shared");
		 model.put("itmhd", "List of Item Shared");
		 model.put("itmdsc", "List of item shared by you.");
		return "/ag/content/share/viewmyshareitems";
	}
	
	//view all shared items for borrowing
	@GetMapping("/share/items/{category}")
	public String displaySharingItems(@PathVariable String category, ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		ArrayList<ShareItem> itemsLst = new ArrayList<ShareItem>();
		try {
			if("all".equals(category)) {				
				itemsLst = itemDataHandler.getSharingItemsStartingFromLatest("", activeUser.getuId());
			}else {
				itemsLst = itemDataHandler.getSharingItemsOrderByCat(category, "", activeUser.getuId());
			}
			
		} catch (Exception e) {
			log.info("displayItems error "+e);
		}
		if(itemsLst.size() < 15) {
			model.put("lstitem", "end");
		}else {
			model.put("lstitem", itemsLst.get(itemsLst.size() - 1).getId());
		}

		model.put("category", category);	
		 model.put("itemslist", itemsLst);
		 model.put("catmap", getSharingCategoryMap());
		return "/ag/content/share/listshareitems";
	}
	//load additional shared items for borrowing
	@PostMapping("/share/items/nextitems")
	public String displayNextSetShareItems(ModelMap model, @RequestParam("catgry") String category, @RequestParam("curval") String lastitem) {	
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		ArrayList<ShareItem> itemsLst = new ArrayList<ShareItem>();

		try {
			if("all".equals(category)) {
				itemsLst = itemDataHandler.getSharingItemsStartingFromLatest(lastitem, activeUser.getuId());
			}else {
				itemsLst = itemDataHandler.getSharingItemsOrderByCat(category, lastitem, activeUser.getuId());
			}
			
		} catch (Exception e) {
			log.info("displayItems error "+e);
		}
		if(itemsLst.size() < 15) {
			model.put("lstitem", "end");
		}else {
			model.put("lstitem", itemsLst.get(itemsLst.size() - 1).getId());
		}			
		 model.put("itemslist", itemsLst);
		 model.put("catmap", getSharingCategoryMap());
		return "/ag/content/share/ajaxitems";
	}
	//updated details of user shared item
	@GetMapping("/share/item/modify/{itemid}")
	public String modifyShareItem(@PathVariable String itemid, ModelMap model) {
		ShareItemUI shareItemUI;
		try {
			ShareItem sitem = itemDataHandler.getShareItem(itemid);
			shareItemUI = getShareItemUi(sitem);
		} catch (Exception e) {
			shareItemUI = new ShareItemUI();
			log.info("modify share item  error "+e);
		}
		model.put("shareItemUI", shareItemUI);	
		return "/ag/content/share/addshareitem";
	}
	private ShareItemUI getShareItemUi(ShareItem sitem) {
		ShareItemUI agitemui = new ShareItemUI();
		agitemui.setCategory(sitem.getCat());
		agitemui.setDescription(sitem.getDesc());
		agitemui.setId(sitem.getId());
		agitemui.setName(sitem.getName());
		agitemui.setPtime(sitem.getPtime());		
		return agitemui;
	}
	//get delete shared item
	@GetMapping("/share/item/delete/{itemid}")
	public String deleteShareItem(@PathVariable String itemid, ModelMap model) {
		
		model.put("itemid", itemid);
		model.put("deleted", "n");
		return "/ag/content/share/deleteitem";
	}
	// delete shared item
	@GetMapping("/share/item/delete/{answ}/{itemid}")
	public String deleteShareItemYesNo(@PathVariable String answ, @PathVariable String itemid, ModelMap model) {
		if(answ.equals("accept")) {
			try {
				itemDataHandler.deleteShareItem(itemid);
			} catch (Exception e) {				
				log.info("deleteItem error "+e);
				return "redirect:/share/myitems/view";
			}
			model.put("deleted", "y");
			return "/ag/content/share/deleteitem";
		}		
		
		return "redirect:/share/myitems/view";
	}
	/**
	 * View item details for borrowing
	 */
	@GetMapping("/share/item/borrow/view/{itemid}")
	public String borrowViewShareItem(@PathVariable String itemid, ModelMap model) {
		ShareItem sitem;
		AgUser aguser;
		try {
			 sitem = itemDataHandler.getShareItem(itemid);
			 aguser = userDataHandler.getUserById(sitem.getuId());
			 
		} catch (Exception e) {
			sitem = new ShareItem();
			aguser = new AgUser();
			log.info("modify share item  error "+e);
		}
		model.put("sitem", sitem);	
		model.put("aguser", aguser);
		model.put("borrow", "n");
		return "/ag/content/share/borrowitem";				
	}
	/**
	 * Borrow item
	 */
	@GetMapping("/share/item/borrow/{itemid}")
	public String borrowShareItem(@PathVariable String itemid, ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		model.put("borrow", "y");
		try {
			itemDataHandler.updateShareItemBorrowStatus(itemid, activeUser.getuId());	
			ShareItem sitem = itemDataHandler.getShareItem(itemid);
			AgUser auser = userDataHandler.getUserById(sitem.getuId());
			emailManager.borrowItemInformOwner(auser.getEmail(), sitem.getName(), activeUser.getResUnit());	
		} catch (Exception e) {
			model.put("borrow", "e");
			log.info(" error "+e);
		}
			
		return "/ag/content/share/borrowitem";				
	}
	//List of items borrowed by user
	@GetMapping("/share/borrow/byuser")
	public String borrowedItems(ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		ArrayList<ShareItem> itemsLst = new ArrayList<ShareItem>();
		try {
		
			itemsLst = itemDataHandler.getOthersSharedItemsBorrowedByUser(activeUser.getuId());
		} catch (Exception e) {
			log.info("borrow  error "+e);
		}

		model.put("itemslist", itemsLst);
		 model.put("catmap", getSharingCategoryMap());
		 
		return "/ag/content/share/userborrowed";
	}
	/**
	 * List of user items borrowed by others
	 */
	@GetMapping("/share/borrow/byothers")
	public String myItemsBorrowedByOthers( ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		ArrayList<ShareItem> itemsLst = new ArrayList<ShareItem>();
		try {
			
			itemsLst = itemDataHandler.getSharedItemsBorrowedByOtherUsers(activeUser.getuId());
		} catch (Exception e) {
			log.info("borrow  error "+e);
		}
		
		model.put("itemslist", itemsLst);
		 model.put("catmap", getSharingCategoryMap());
		return "/ag/content/share/othersborrowed";
	}
	/**
	 * Self update to change the status of item borrowed by others and returned it back.
	 */
	@GetMapping("/share/borrow/byothers/delete/{itemid}")
	public String borrowSelfUpdateShareItem(@PathVariable String itemid, ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		try {

			ShareItem si = itemDataHandler.getShareItem(itemid);
			
			if(!si.getuId().equals(activeUser.getuId())) {
				return "/ag/common/error";
			}
			
			itemDataHandler.updateShareItemBorrowStatus(itemid, "");
		} catch (Exception e) {
			log.info("update share item  error "+e);
		}
		model.put("status", "u");
		return "/ag/content/share/status";
	}
	/**
	 * Remind user to return the borrowed item
	 */
	@GetMapping("/share/borrow/byothers/reminder/{itemid}")
	public String borrowNotifyShareItem(@PathVariable String itemid, ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		try {
			
			ShareItem si = itemDataHandler.getShareItem(itemid);
			
			if(!si.getuId().equals(activeUser.getuId())) {
				return "/ag/common/error";
			}
			
			AgUser au = userDataHandler.getUserById(si.getbUid());
			emailManager.remindBorrowerReturnItem(au.getEmail(), si.getName(), au.getUnitId());			
		} catch (Exception e) {
			log.info("notify borrower err "+e);
		}
		model.put("status", "n");
		return "/ag/content/share/status";
	}

	

	private Map<String, String> getSharingCategoryMap() {
		Map<String, String> catMap = new HashMap<String, String>();
		catMap.put("cb", "Children's Book");
		catMap.put("ab", "Adults Book");
		catMap.put("ac", "Academic Book");
		catMap.put("ty", "Toys");
		catMap.put("ut", "Utilities");
		catMap.put("ot", "Other");
		return catMap;
	}

}
