package com.zenzsol.allygain.data.dto.ui;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ReservationUI {
	boolean submitted;
	private String id;
	private String resourceId;
	private String bookingdate;
	@Min(value = 0, message = "It should be between 00 to 23 hr")
    @Max(value = 23, message = "It should be between 00 to 23 hr")
	private int startTime;
	public boolean isSubmitted() {
		return submitted;
	}
	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}	
	public String getBookingdate() {
		return bookingdate;
	}
	public void setBookingdate(String bookingdate) {
		this.bookingdate = bookingdate;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}	
}
