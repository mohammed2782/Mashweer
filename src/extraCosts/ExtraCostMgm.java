package extraCosts;

import com.app.core.CoreMgr;

public class ExtraCostMgm extends CoreMgr {
	public ExtraCostMgm (){
		MainSql = " select exc_id , exc_costcat, exc_amount , exc_costdate , exc_createdby , exc_createddt , exc_rmk ,"+
					" concat(year(exc_costdate),' - ',Month(exc_costdate)) as MonthYear "
					+ " from m_extracost";
		canNew = true;
		canDelete = true;
		canEdit = true;
		canFilter = true;
		
		UserDefinedPageRows = 100;
		
		mainTable = "m_extracost";
		keyCol = "exc_id";
		userDefinedCaption = "أدارة التكاليف";
		
		userDefinedGroupByCol = "MonthYear";
		userDefinedGroupColsOrderBy = "exc_costdate";
		
		userDefinedGroupSortMode = "desc";
		userDefinedSumCols.add("exc_amount");
		
		userDefinedFilterCols.add("exc_costcat");
		
		
		
		//userDefinedGridCols.add("exc_id");
		userDefinedGridCols.add("exc_costdate");
		userDefinedGridCols.add("exc_costcat");
		userDefinedGridCols.add("exc_amount");
		userDefinedGridCols.add("exc_rmk");
		userDefinedGridCols.add("exc_createddt");
		userDefinedGridCols.add("exc_createdby");
		
		userDefinedNewColsHtmlType.put("exc_rmk", "TEXTAREA");
		
		userDefinedColLabel.put("exc_id", "شفرة");
		userDefinedColLabel.put("exc_costcat", "نوع التكلفة");
		userDefinedColLabel.put("exc_amount", "المبلغ");
		userDefinedColLabel.put("exc_costdate", "تاريخ التكلفة");
		userDefinedColLabel.put("exc_createdby", "أدخل من خلال");
		userDefinedColLabel.put("exc_createddt", "تاريخ أدخال السجل");
		userDefinedColLabel.put("exc_rmk", "ملاحظات");
		
		userDefinedNewCols.add("exc_costcat");
		userDefinedNewCols.add("exc_amount");
		userDefinedNewCols.add("exc_costdate");
		userDefinedNewCols.add("exc_rmk");
		
		userDefinedEditCols.add("exc_costcat");
		userDefinedEditCols.add("exc_amount");
		userDefinedEditCols.add("exc_costdate");
		userDefinedEditCols.add("exc_rmk");
		
		userDefinedLookups.put("exc_costcat", "select cost_id , cost_name from kbextracost");
		userDefinedNewColsDefualtValues.put("exc_createdby", new String [] {"{useridlogin}"});
	}
}

