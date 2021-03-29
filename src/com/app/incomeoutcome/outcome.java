package com.app.incomeoutcome;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class outcome extends CoreMgr {
	public outcome(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		
			MainSql = "select *,'' as fromdate ,'' as todate from p_outcomes ";
			mainTable = "p_outcomes";
			keyCol   = "ou_id";
			
			/*
			 * to define outcomes gridviews caption
			 */
			userDefinedCaption = " المصروفات";
			newCaption = "إضافة  مصروف";
			updCaption = "تعديل بيانات مصروف";
			 
			/*
			 * to enable/disable basic operations 
			 */
			canNew = true;
			canFilter =  true;
			canEdit = true;
			canDelete = true;

			/*
			 * to define gridview columns that want to show to outcome
			 */
			//userDefinedGridCols.add("ou_id");
			userDefinedGridCols.add("ou_item");
			userDefinedGridCols.add("ou_price");
			userDefinedGridCols.add("ou_date");
			userDefinedGridCols.add("ou_createdby");
			userDefinedGridCols.add("ou_rmk");
			
			
			/*
			 * to define gridview label that want to show to outcomes
			 */
			userDefinedColLabel.put("ou_id", "رقم المصروف ");
			userDefinedColLabel.put("ou_item", "إسم المصروف ");
			userDefinedColLabel.put("ou_price", "المبلغ");
			userDefinedColLabel.put("ou_date", "تاريخ الدفع");
			userDefinedColLabel.put("ou_createdby", "صرفت عن طريق");
			userDefinedColLabel.put("ou_rmk", "ملاحظات");
			userDefinedColLabel.put("fromdate", "من تاريخ");
			userDefinedColLabel.put("todate", "الى تاريخ");
			

			/*
			 * to define new columns for insert operation
			 */
			userDefinedNewCols.add("ou_item");
			userDefinedNewCols.add("ou_price");
			userDefinedNewCols.add("ou_date");
			//userDefinedNewCols.add("ou_entrydt");
			userDefinedNewCols.add("ou_rmk");
			userDefinedNewCols.add("ou_createdby");
			
			userDefinedColsMustFill.add("ou_item");
			userDefinedColsMustFill.add("ou_price");
			userDefinedColsMustFill.add("ou_date");
			userDefinedColsMustFill.add("ou_createdby");
			
			/*
			 * to define filter columns for search operation
			 */
			userDefinedFilterCols.add("ou_item");
			//userDefinedFilterCols.add("ou_price");
			//userDefinedFilterCols.add("ou_date");
			userDefinedFilterCols.add("fromdate");
			userDefinedFilterCols.add("todate");
			
			userDefinedFilterColsHtmlType.put("fromdate", "DATE");
			userDefinedFilterColsHtmlType.put("todate", "DATE");
			userDefinedFilterColsHtmlType.put("ou_item", "DROPLIST");
			
			/*
			 * to define edit coulmns for update opeartion
			 */
			userDefinedEditCols.add("ou_item");
			userDefinedEditCols.add("ou_price");
			userDefinedEditCols.add("ou_date");
			userDefinedEditCols.add("ou_rmk");

			userDefinedNewColsHtmlType.put("ou_rmk", "TEXTAREA");
			
			userDefinedReadOnlyNewCols.add("ou_createdby");
			userDefinedNewColsDefualtValues.put("ou_createdby", new String[] {"{useridlogin}"});
			
			/*
			 * to define lookup coulmns 
			 */
			userDefinedLookups.put("ou_item", "select co_id,co_name from kbcost_type order by co_name desc ");
			
		}//end of constructor outcome
	public void initialize(HashMap smartyStateMap){
		boolean foundSearch = false;
		super.initialize(smartyStateMap);
		String fromDate = "", toDate = "";
		if (search_paramval !=null ) {

			if (search_paramval.get("todate")!=null && search_paramval.get("fromdate")!=null||search_paramval.get("lim_itemid")!=null) {
				for (String parameter : search_paramval.keySet()) {
					for (String value : search_paramval.get(parameter)) {
						if (!parameter.equals("filter") && (value != null)
								&& (!value.equals(""))) {
							if (parameter.equals("fromdate")) {
								fromDate=value;
								foundSearch = true;
								
							} else if (parameter.equals("todate")) {
								toDate=value;
								foundSearch = true;
							}
						}
					}
				}
			}
		}
		if(toDate.isEmpty()) {
			DateTimeFormatter Dateformat =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime now = LocalDateTime.now();
			toDate = Dateformat.format(now);			
		}
		if(fromDate.isEmpty()) {
			String startTime = "2020-04-04T17:48:23.558";
			LocalDateTime localDateTime = LocalDateTime.parse(startTime);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			fromDate = localDateTime.format(formatter);	
		}
		
		if (foundSearch) {
			MainSql = "select p_outcomes.*,'' as fromdate ,'' as todate from p_outcomes"
					+ " where ou_date >= '"+fromDate+"' and ou_date<adddate(date('"+toDate+"'),1)";
			
			
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdate");
		search_paramval.remove("todate");
		return super.genListing();
	}
		
				
	}//end of class outcome
