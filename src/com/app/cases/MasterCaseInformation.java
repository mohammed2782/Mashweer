package com.app.cases;

public class MasterCaseInformation {
	private int caseMasterId;
	private String custName;
	private String hp;
	private String mapLat;
	private String mapLongt;
	private String state;
	private String city;
	private String district;
	private String parties = "N";
	private String locationDetails;
	private int senderCompanyId;
	private String senderCompanyName;
	
	public String toString() {
		String s = "custName="+this.custName;
		s +=", hp="+this.hp;
		s +=", mapLat="+this.mapLat;
		s +=", mapLongt="+this.mapLongt;
		s +=", state="+this.state;
		s +=", city="+this.city;
		s +=", district="+this.district;
		s +=", parties="+this.parties;
		s +=", locationDetails="+this.locationDetails;
		return s;
	}
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getHp() {
		return hp;
	}
	public void setHp(String hp) {
		this.hp = hp;
	}
	public String getMapLat() {
		return mapLat;
	}
	public void setMapLat(String mapLat) {
		this.mapLat = mapLat;
	}
	public String getMapLongt() {
		return mapLongt;
	}
	public void setMapLongt(String mapLongt) {
		this.mapLongt = mapLongt;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getParties() {
		return parties;
	}
	public void setParties(String parties) {
		this.parties = parties;
	}
	public String getLocationDetails() {
		return locationDetails;
	}
	public void setLocationDetails(String locationDetails) {
		this.locationDetails = locationDetails;
	}

	public int getCaseMasterId() {
		return caseMasterId;
	}

	public void setCaseMasterId(int caseMasterId) {
		this.caseMasterId = caseMasterId;
	}

	

	public String getSenderCompanyName() {
		return senderCompanyName;
	}

	public void setSenderCompanyName(String senderCompanyName) {
		this.senderCompanyName = senderCompanyName;
	}

	public int getSenderCompanyId() {
		return senderCompanyId;
	}

	public void setSenderCompanyId(int senderCompanyId) {
		this.senderCompanyId = senderCompanyId;
	}
	
}
