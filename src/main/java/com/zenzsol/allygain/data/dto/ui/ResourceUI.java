package com.zenzsol.allygain.data.dto.ui;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ResourceUI {
	boolean submitted;
	
	private String id;
	
	@NotEmpty(message = "Resource or facility name cann't be empty.")
    @Size(min = 5, max = 100, message = "Enter valid resource or facility name.")
	private String name;
	
	@Min(value = 0, message = "It should be between 00 to 23 hr")
    @Max(value = 23, message = "It should be between 0 to 23 hr")
	private int opensAt;
	
	@Min(value = 1, message = "It should be between 01 to 23 hr")
    @Max(value = 23, message = "It should be between 01 to 23 hr")
	private int closesAt;
	
	@Min(value = 1, message = "It should be between 1 to 24 hrs")
    @Max(value = 24, message = "It should be between 1 to 24 hrs")
	private int reservationDuration;
	
	private String comments;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOpensAt() {
		return opensAt;
	}

	public void setOpensAt(int opensAt) {
		this.opensAt = opensAt;
	}

	public int getClosesAt() {
		return closesAt;
	}

	public void setClosesAt(int closesAt) {
		this.closesAt = closesAt;
	}

	public int getReservationDuration() {
		return reservationDuration;
	}

	public void setReservationDuration(int reservationDuration) {
		this.reservationDuration = reservationDuration;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	


}
