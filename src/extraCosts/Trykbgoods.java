package extraCosts;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class Trykbgoods extends CoreMgr{
	public Trykbgoods() {
		MainSql = "select kbgoods.* , '' as fake from kbgoods";
		// 	gname 	 	 	 	
		userDefinedColLabel.put("gname", "Name of Goods");
		userDefinedColLabel.put("g_defprice", "Buy Price");
		userDefinedColLabel.put("g_defsellprice", "Sell Price Initial");
		userDefinedColLabel.put("g_supplier", "Supplier");
		userDefinedColLabel.put("g_cont_capacity", "Capacity %");
		
		userDefinedGridCols.add("gname");
		userDefinedGridCols.add("g_defsellprice");
		userDefinedGridCols.add("g_cont_capacity");
		userDefinedGridCols.add("fake");
		
		canFilter = true;
		userDefinedFilterCols.add("g_supplier");
		userDefinedFilterCols.add("g_defsellprice");
		
		userDefinedLookups.put("g_supplier", "SELECT supp_id , supp_name FROM kbsupplier");
		
		mainTable = "kbgoods";
		canNew = true;
		userDefinedNewCols.add("g_supplier");
		userDefinedNewCols.add("gname");
		userDefinedNewCols.add("g_cont_capacity");
		userDefinedNewCols.add("g_defsellprice");
		
		canDelete = true;
		keyCol = "g_id";
		myhtmlmgr.refreshPageOnDelete = true;
		
		canEdit = true;
		
		userDefinedFilterColsHtmlType.put("g_supplier", "CHECKBOX");
		
		userDefinedCaption = "تجربة لابات ولبنا </br>في يوم 3 مارس";
		
		userDefinedGroupByCol = "g_supplier";
		
		userDefinedSumCols.add("g_defsellprice");
		slidingGroups = true;
		
		userModifyTD.put("fake", "changeMyFakecolumn({g_id})");
	}
	public String changeMyFakecolumn(HashMap<String , String> p) {
		String myId = p.get("g_id");
		
		return "<td><button name = 'mybtn' value='"+myId+"' ></button></td>";
	}
}
