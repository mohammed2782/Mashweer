package com.app.setup;

import com.app.core.CoreMgr;

public class setup_goods extends CoreMgr{
public setup_goods (){
			
		MainSql =" select * From kbgoods ";
		
		userDefinedNewCols.add("g_supplier");
		userDefinedNewCols.add("gname");
		userDefinedNewCols.add("g_defprice");
		userDefinedNewCols.add("g_defsellprice");
		userDefinedNewCols.add("g_cont_capacity");
		
		userDefinedEditCols.add("g_supplier");
		userDefinedEditCols.add("gname");			
		userDefinedEditCols.add("g_defprice");
		userDefinedEditCols.add("g_defsellprice");
		userDefinedEditCols.add("g_cont_capacity");
		
		userDefinedColsMustFill.add("g_supplier");
		userDefinedColsMustFill.add("gname");
		userDefinedColsMustFill.add("g_defprice");
		userDefinedColsMustFill.add("g_defsellprice");
		
		userDefinedGridCols.add("g_id");
		userDefinedGridCols.add("g_cont_capacity");
		userDefinedGridCols.add("g_defprice");
		userDefinedGridCols.add("g_defsellprice");
		userDefinedGridCols.add("gname");
		userDefinedGridCols.add("g_supplier");
		
		
		userDefinedColLabel.put("g_id", "رقم السلعة");
		userDefinedColLabel.put("gname", " وصف السلعة");
		userDefinedColLabel.put("g_defprice", " سعر شراء  أولي");
		userDefinedColLabel.put("g_defsellprice", " سعر بيع  أولي");
		userDefinedColLabel.put("g_supplier", "المجهز");
		userDefinedColLabel.put("g_cont_capacity" , "سعة الحاوية  - بالكارتون");
		
		userColHintEDIT.put("g_defprice", "سعر أولي للسلعة في حال</BR> أن المستخدم لم يدخل سعر السلعة أثاء الشراء");
		
		userDefinedLookups.put("g_supplier", "select supp_id,supp_name From kbsupplier order by supp_name asc");
		
		//userColHintEDIT.put("gname","blacky");
		
		keyCol = "g_id";
		mainTable ="kbgoods";
		
		search_paramval = null;
		canNew =true;
		canEdit = true;
		canDelete = true;
		userDefinedCaption = "أعدادات السلع";
		
		clickableRow = true;
		userDefinedGlobalClickRowID="goodid";
	}
}
