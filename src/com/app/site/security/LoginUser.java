package com.app.site.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.core.smartyLogAndErrorHandling;

import com.app.db.mysql;
import com.app.session.SessionVars;

public class LoginUser {
	private  String userID = "";
	private int usid;
	private int compID;
	public int getCompID() {
		return compID;
	}

	public void setCompID(int compID) {
		this.compID = compID;
	}
	
	private  String userName_ar ="";
	private  String userName_en ="";
	private  int rank = 0;
	private  boolean firstTimeLogin = false;
	private  String superRank="N";
	private  String superIT  = "N";
	private  String rank_code ="";
	private  String div_code ="";
	private  String dep_code ="";
	private  String storeCode = "";
	//private  String userProfileID = "";
	private  String errorMsg ="";
	private  String warningMsg ="";
	private  boolean errorFlag = false;
	private  boolean warningFlag = false;
	private  boolean loggedIn = false;
	private  boolean canEdit  = false;
	private  boolean canDelete = false;
	private  boolean canNew    = false;
	private  boolean canApprove = false;
	private  boolean staff	 = false;
	private  boolean haveDashBoard = false;
	
	public  HashMap <String ,String> credentials =null;
	
	
	public  void doLogout(){
		setLoggedIn(false);	
	}
	
	public void dologincompany(String pass) {
		setLoggedIn(false);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String MD5pass ="";
		boolean activeUser = false;
		
		try{
			conn = mysql.getConn();
			pst = conn.prepareStatement("select md5(?) from dual");
			pst.setString(1, pass);
			rs = pst.executeQuery();
			if (rs.next()){
				MD5pass = rs.getString(1);
			}
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			
			pst = conn.prepareStatement("select * from kbcompanies where lower(comp_loginid)=lower(?) and comp_pwd=?  ");
			pst.setString(1,getUserID().toLowerCase());
			pst.setString(2, MD5pass);
			rs = pst.executeQuery();
			if (rs.next()){
				setLoggedIn(true);
				if (rs.getString("comp_active").equalsIgnoreCase("Y"))
					activeUser = true;
				setCompID(rs.getInt("comp_id"));
			}
			
			if (activeUser) {
				setStaff(true);
				userName_ar = rs.getString("comp_name");
				userName_en = rs.getString("comp_name");
				superRank   = "N";
				setRank_code("COMPVIEWONLY");
				setSuperIT("N"); 
				setStoreCode("BAS");
			}else{
				setStaff(false);
				setErrorMsg("This User is not Active, Call Admin to activate");
				setErrorFlag(true);
				setLoggedIn(false);
				
			}
			if(!isLoggedIn()) {
				setErrorMsg("Wrong ID or Password");
				setErrorFlag(true);
			}
			
		}catch(Exception e){
			/*Login the error please*/
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			setErrorMsg("System Error , Please Tell the Admin");
			setErrorFlag(true);
			setLoggedIn(false);
			e.printStackTrace();	
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			try{conn.close();}catch(Exception e){}
		}
		
	}
	
	public  boolean doLogin(String pass){
		setLoggedIn(false);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String MD5pass ="";
		boolean activeUser = false;
		try{
			conn = mysql.getConn();
			pst = conn.prepareStatement("select md5(?) from dual");
			pst.setString(1, pass);
			rs = pst.executeQuery();
			if (rs.next()){
				MD5pass = rs.getString(1);
			}
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			rs = null;
			pst = conn.prepareStatement("select * from kbusers where lower(us_loginid)=? and us_password=? and us_active='Y'");
			pst.setString(1,getUserID().toLowerCase());
			pst.setString(2, MD5pass);
			rs = pst.executeQuery();
			if (rs.next()){
				setLoggedIn(true);
				if (rs.getString("us_active").trim().equalsIgnoreCase("Y"))
					activeUser = true;
			}
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}	
			if (activeUser) {
				setStaff(true);
				if (LoadUserCredentials(conn)){// have error
					setLoggedIn(false);
					return loggedIn;
				}
			}else{
				setStaff(false);
				setErrorMsg("This User is not Active, Call Admin to activate");
				setErrorFlag(true);
				setLoggedIn(false);
				return loggedIn;
			}
			if(!isLoggedIn()) {
				setErrorMsg("Wrong ID or Password");
				setErrorFlag(true);
			}
			try{conn.close();}catch(Exception e){}
			return loggedIn;
		}catch(Exception e){
			/*Login the error please*/
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			setErrorMsg("System Error , Please Tell the Admin");
			setErrorFlag(true);
			setLoggedIn(false);
			e.printStackTrace();	
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			try{conn.close();}catch(Exception e){}
		}
		return loggedIn;
	}
	
	
	/*
	 * 
	 * TO load the staff credentials
	 */
	public  boolean LoadUserCredentials(Connection conn){
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		System.out.println("loading Staff credentials");
		setErrorFlag(false);
		try{
			pst = conn.prepareStatement("select us_id, us_name ,rank_super ,rank_super_it, us_rank, us_storecode "
										+ " from kbusers "
										+ " join kbrank on rank_code = us_rank"
										+ " where lower(us_loginid)=? ");
			pst.setString(1,getUserID().toLowerCase());
			rs = pst.executeQuery();
			while (rs.next()){
				usid = rs.getInt("us_id");
				userName_ar = rs.getString("us_name");
				userName_en = rs.getString("us_name");
				superRank   = rs.getString("rank_super");
				setRank_code(rs.getString("us_rank"));
				setSuperIT(rs.getString("rank_super_it")); 
				setStoreCode(rs.getString("us_storecode"));
			}
			if (superRank.equals("Y")){
				setHaveDashBoard(true);
				setErrorFlag(false);
				try{rs.close();}catch(Exception e){}
				try{pst.close();}catch(Exception e){}
				return isErrorFlag();// no need to load any Credentials.
			}
			else{
				if ((rank_code ==null) || (rank_code.trim().equals(""))){
					errorMsg = "rank_code is missing for this User";
					setErrorFlag(true);
					try{rs.close();}catch(Exception e){}
					try{pst.close();}catch(Exception e){}
					return isErrorFlag();
				}
			}
		}catch(Exception e){
			/*Login the error please*/
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			e.printStackTrace();
			
		}finally{
			try{if(rs!=null)rs.close();}catch(Exception e){}
			try{if(pst!=null)pst.close();}catch(Exception e){}
		}
		return isErrorFlag();
	}

	public  String getUserID() {
		return userID;
	}

	public  void setUserID(String userID) {
		this.userID = userID;
	}

	

	public  boolean isLoggedIn() {
		return loggedIn;
	}

	public  void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	public  int getRank() {
		return rank;
	}

	public  void setRank(int rank) {
		this.rank = rank;
	}

	public  String getDiv_code() {
		return div_code;
	}

	public  void setDiv_code(String div_code) {
		this.div_code = div_code;
	}

	public  String getDep_code() {
		return dep_code;
	}

	public  void setDep_code(String dep_code) {
		this.dep_code = dep_code;
	}

	/*
	public  String getUserProfileID() {
		return userProfileID;
	}

	public  void setUserProfileID(String userProfileID) {
		Users.userProfileID = userProfileID;
	}
*/
	public  String getRank_code() {
		return rank_code;
	}

	public  void setRank_code(String rank_code) {
		this.rank_code = rank_code;
	}

	public  String getErrorMsg() {
		return errorMsg;
	}

	public  void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public  String getWarningMsg() {
		return warningMsg;
	}

	public  void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}

	public  String getSuperRank() {
		return superRank;
	}

	public  void setSuperRank(String superRank) {
		this.superRank = superRank;
	}

	public  boolean isErrorFlag() {
		return errorFlag;
	}

	public  void setErrorFlag(boolean errorFlag) {
		this.errorFlag = errorFlag;
	}

	public  boolean isWarningFlag() {
		return warningFlag;
	}

	public  void setWarningFlag(boolean warningFlag) {
		this.warningFlag = warningFlag;
	}

	public  boolean isCanEdit() {
		return canEdit;
	}

	public  void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public  boolean isCanDelete() {
		return canDelete;
	}

	public  void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public  boolean isCanNew() {
		return canNew;
	}

	public  void setCanNew(boolean canNew) {
		this.canNew = canNew;
	}
	
	public  String getGreetings(String lang){
		if (lang.equalsIgnoreCase("AR")){
			return userName_ar;
		}else{
			return userName_en;
		}
		
	}

	public  String getSuperIT() {
		return superIT;
	}

	public  void setSuperIT(String superIT) {
		this.superIT = superIT;
	}

	public  boolean isStaff() {
		return staff;
	}

	public  void setStaff(boolean staff) {
		this.staff = staff;
	}

	public  boolean isCanApprove() {
		return canApprove;
	}

	public  void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}

	public boolean isHaveDashBoard() {
		return haveDashBoard;
	}

	public void setHaveDashBoard(boolean haveDashBoard) {
		this.haveDashBoard = haveDashBoard;
	}
	public boolean isFirstTimeLogin() {
		return firstTimeLogin;
	}

	public void setFirstTimeLogin(boolean firstTimeLogin) {
		this.firstTimeLogin = firstTimeLogin;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * @return the usid
	 */
	public int getUsid() {
		return usid;
	}

	/**
	 * @param usid the usid to set
	 */
	public void setUsid(int usid) {
		this.usid = usid;
	}

}


