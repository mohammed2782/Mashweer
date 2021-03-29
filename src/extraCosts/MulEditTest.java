package extraCosts;

import com.app.core.CoreMgr;

public class MulEditTest extends CoreMgr {
	public MulEditTest() {
		MainSql = "select * from kbgoods";
		
		mainTable = "kbgoods";
		keyCol = "g_id";
		
		canEdit = true;
		
		displayMode = "GRIDEDIT";
		/*userDefinedGridCols.add("g_id");
		userDefinedGridCols.add("gname");
		userDefinedGridCols.add("g_defprice");
		userDefinedGridCols.add("");
		*/
		
		userDefinedEditCols.add("g_defprice");
		userDefinedEditCols.add("g_supplier");
		userDefinedLookups.put("g_supplier", "SELECT supp_id , supp_name FROM kbsupplier");
		
		userDefinedColsMustFill.add("g_defprice");
		userDefinedColsMustFill.add("g_supplier");
		
		//userDefinedEditColsHtmlType.put("defprice","");
	}

}
