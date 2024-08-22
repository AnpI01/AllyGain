package com.zenzsol.allygain.data.dto.db;

import com.google.cloud.Timestamp;

public class ShareItem {

	private String id;
	private String cId;
	private String uId;

	private String name;
	private String desc;
	private String cat;
	private String image;
	private int ptime;
	private String bUid;
	private Timestamp cDt;
	
	public ShareItem() {
		image="";
		bUid="";
	}
	
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
	public String getuId() {
		return uId;
	}
	public void setuId(String uId) {
		this.uId = uId;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getPtime() {
		return ptime;
	}
	public void setPtime(int ptime) {
		this.ptime = ptime;
	}	

	public String getbUid() {
		return bUid;
	}
	public void setbUid(String bUid) {
		this.bUid = bUid;
	}
	public Timestamp getcDt() {
		return cDt;
	}
	public void setcDt(Timestamp cDt) {
		this.cDt = cDt;
	}
}
