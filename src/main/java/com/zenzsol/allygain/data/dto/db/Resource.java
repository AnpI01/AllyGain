package com.zenzsol.allygain.data.dto.db;

public class Resource {
	private String id;
	//community id
	private String cId;
	private String name;
	//facility opens at
	private int oAt;
	//facility closes at
	private int cAt;
	//allowed duration for reservation
	private int rd;
	private String comments;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getoAt() {
		return oAt;
	}
	public void setoAt(int oAt) {
		this.oAt = oAt;
	}
	public int getcAt() {
		return cAt;
	}
	public void setcAt(int cAt) {
		this.cAt = cAt;
	}
	public int getRd() {
		return rd;
	}
	public void setRd(int rd) {
		this.rd = rd;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}


	
}
