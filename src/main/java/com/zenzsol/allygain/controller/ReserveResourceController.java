package com.zenzsol.allygain.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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

import com.google.cloud.Timestamp;
import com.zenzsol.allygain.data.db.ReservationDataHandler;
import com.zenzsol.allygain.data.db.UserDataHandler;
import com.zenzsol.allygain.data.dto.db.Reservation;
import com.zenzsol.allygain.data.dto.db.Resource;
import com.zenzsol.allygain.data.dto.ui.ReservationUI;
import com.zenzsol.allygain.security.AGUserDetails;
import com.zenzsol.allygain.util.CommonUtils;

import jakarta.validation.Valid;

@Controller
public class ReserveResourceController {
	private static final Logger log = Logger.getLogger(ReserveResourceController.class.getName());
	@Autowired
	private UserDataHandler userDataHandler;
	@Autowired
	private ReservationDataHandler resDataHandler;
	
	@GetMapping("/reserve/view")
	public String viewMyReservations(ModelMap model) {
	
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
										.getContext().getAuthentication().getPrincipal();

		Map<String, Resource> resMap = new HashMap<String, Resource>();
		ArrayList<Reservation> itmslst = new ArrayList<Reservation>();
		try {
			itmslst = resDataHandler.getLastSixMonthsReservationsByUser(activeUser.getuId());
			resMap = resDataHandler.getResourcesMapIdKey(activeUser.getCommunityId());
		} catch (Exception e) {
			log.info("view reservations error "+e);
		} 
		model.put("itemslist", itmslst);	
		model.put("resmap", resMap);	
		return "/ag/content/reserve/viewreservations";
	}
	@GetMapping("/reserve/book")
	public String getbookResource(ModelMap model) {
		model.put("reservationUI", new ReservationUI());		

		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();


		model.put("comRes", getResourceList(activeUser.getCommunityId()));		
		return "/ag/content/reserve/bookresource";
	}
	private ArrayList<Resource> getResourceList(String communityId){
		ArrayList<Resource> comRes = new ArrayList<Resource>();
		try {
			comRes = userDataHandler.getResourcesByCommunity(communityId);
		} catch (Exception e) {
			log.info("bookResource error "+e);
		}
		return comRes;
	}
	@PostMapping("/reserve/book")
	public String bookResource(@Valid ReservationUI reservationUI, BindingResult result, ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();

		validateReservation(reservationUI, result, activeUser.getCommunityId());

		if (!result.hasErrors()) {
			       
			try {
				Reservation r = createReservationDBObject(reservationUI, activeUser.getCommunityId(),activeUser.getuId());
				resDataHandler.addReservation(r);				
			} catch (Exception e) {
				log.info("bookResource error "+e);
				result.addError(new ObjectError("message", "Booking failed."));
				return "/ag/content/reserve/bookresource";
			}
			reservationUI.setSubmitted(true);
		}else {
			model.put("comRes", getResourceList(activeUser.getCommunityId()));		
		}
		return "/ag/content/reserve/bookresource";
	}
	private Reservation createReservationDBObject(ReservationUI reservationUI, String communityId, String uid) {
		Reservation reservation =  new Reservation();
		reservation.setId(reservationUI.getId());
		reservation.setcId(communityId);
		reservation.setuId(uid);
		reservation.setrId(reservationUI.getResourceId());
		reservation.setDt(Timestamp.of(CommonUtils.getDateDdmmyyyy(reservationUI.getBookingdate())));
		reservation.setSt(reservationUI.getStartTime());

		return reservation;
	}

	private boolean validateReservation(ReservationUI reservationUI, BindingResult result, String cid) {
		boolean validity= true;

		Date rdate = CommonUtils.getDateDdmmyyyy(reservationUI.getBookingdate());
		if(rdate == null) {
			result.rejectValue("bookingdate", "reservation.bdt.format");
			return false;
		}else {
			Date td = Calendar.getInstance().getTime();
			if(td.after(rdate)) {
				result.rejectValue("bookingdate", "reservation.bdt.expire");
				return false;
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 3);

			if( cal.getTime().before(rdate) ) {
				result.rejectValue("bookingdate", "reservation.bdt.tooadvance");
				return false;
			}
		}
		//db validation to see if selected resource exists in db
		Resource res = null;
		try {
			res = userDataHandler.getResourceById(reservationUI.getResourceId());
		} catch (Exception e) {
			log.info("selected resource does not exist or incorrect resource id exist validations");

		}
		if(res == null) {
			result.reject("reservation.swrong");
			return false;
		}
		
		//db validation to see if reservation exists for same time period
		ArrayList<Reservation> ress = null;
		try {
			ress = resDataHandler.getReservationsByTime(cid, reservationUI.getResourceId(), Timestamp.of(rdate));
		} catch (InterruptedException | ExecutionException ee) {
			result.reject("reservation.swrong");
			return false;
		}
		if(res != null) {
			int endt = reservationUI.getStartTime()+ res.getRd();  
			for(Reservation resd : ress) {	  
				if(resd.getSt() >= reservationUI.getStartTime()  && resd.getSt() < endt    ) {
					result.rejectValue("startTime", "reservation.exists");
					return false;
				}
			}
		}

		return validity;
	}
	@GetMapping("/reserve/modify/{itemid}")
	public String modifyShareItem(@PathVariable String itemid, ModelMap model) {
		ReservationUI reservationUI;
		
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		
		try {
			Reservation ritem = resDataHandler.getReservation(itemid);
			reservationUI = getReservationUi(ritem);			
			model.put("comRes", getResourceList(activeUser.getCommunityId()));			
		} catch (Exception e) {
			reservationUI = new ReservationUI();
			log.info("modify reservation  error "+e);
		}
		model.put("reservationUI", reservationUI);	
		return "/ag/content/reserve/bookresource";
	}
	private ReservationUI getReservationUi(Reservation ritem) {
		ReservationUI reservationUI = new ReservationUI();
		reservationUI.setBookingdate(CommonUtils.getStringDdmmyyyy(ritem.getDt().toDate()));
		reservationUI.setId(ritem.getId());
		reservationUI.setResourceId(ritem.getrId());
		reservationUI.setStartTime(ritem.getSt());
		
		return reservationUI;
	}
	@GetMapping("/reserve/delete/{itemid}")
	public String deleteShareItem(@PathVariable String itemid, ModelMap model) {
		
		model.put("itemid", itemid);
		model.put("deleted", "n");
		return "/ag/content/reserve/deletereservation";
	}
	@GetMapping("/reserve/delete/{answ}/{itemid}")
	public String deleteReservationYesNo(@PathVariable String answ, @PathVariable String itemid, ModelMap model) {
		if(answ.equals("accept")) {
			try {
				resDataHandler.deleteReservation(itemid);
			} catch (Exception e) {				
				log.info("delete reservation error "+e);
				return "redirect:/reserve/view";
			}
			model.put("deleted", "y");
			return "/ag/content/reserve/deletereservation";
		}		
		
		return "redirect:/reserve/view";
	}
}
