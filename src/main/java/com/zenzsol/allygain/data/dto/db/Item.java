package com.zenzsol.allygain.data.dto.db;

import com.google.cloud.Timestamp;

public class Item {
	private String id;
	private String uId;
	private String cId;
	private String name;
	private String desc;
	private String cat;
	private String img;
	private String price;
	private Timestamp cDt;
	
	public Item() {
		img="";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Timestamp getcDt() {
		return cDt;
	}
	public void setcDt(Timestamp cDt) {
		this.cDt = cDt;
	}
	
	

}
