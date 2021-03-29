package com.app.setup;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class SubGoods extends CoreMgr{
    public SubGoods(){
    	MainSql = "select * from kbsubgoods where subg_gid={goodid}";
    	canEdit = true;
    	canDelete = true;
    	canNew = true;
    	keyCol = "subg_id";
    	mainTable = "kbsubgoods";
    	
    	userDefinedGridCols.add("subg_gid");
    	userDefinedGridCols.add("subg_name");
    	userDefinedGridCols.add("subg_color");
    	userDefinedGridCols.add("subg_createdby");
    	
    	userDefinedColLabel.put("subg_gid", "وصف السلعة");
    	userDefinedColLabel.put("subg_name", "أسم صنف من أصناف السلعة");
    	userDefinedColLabel.put("subg_createdby", "أدخل عن طريق");
    	userDefinedColLabel.put("subg_color", "اللون");
    	
    	userDefinedColsMustFill.add("subg_name");
    	
    	userDefinedReadOnlyEditCols.add("subg_gid");
    	userDefinedReadOnlyEditCols.add("subg_createdby");
    	
    	userDefinedReadOnlyNewCols.add("subg_gid");
    	userDefinedReadOnlyNewCols.add("subg_createdby");
    	
    	userDefinedNewColsDefualtValues.put("subg_createdby", new String [] {"{useridlogin}"});
		//userDefinedEditColsDefualtValues.put("subg_createdby", "{useridlogin}");
    	userDefinedNewColsDefualtValues.put("subg_gid", new String [] {"{goodid}"});
    	
    	//userDefinedDisabledNewCols.add("subg_gid");
    	
    	userDefinedLookups.put("subg_gid", "select g_id, gname from kbgoods where g_id={goodid}");
    	
    	userDefinedNewCols.add("subg_gid");
    	userDefinedNewCols.add("subg_name");
    	userDefinedNewCols.add("subg_color");
    	userDefinedNewCols.add("subg_createdby");
    	
    	userDefinedEditCols.add("subg_name");
    	userDefinedEditCols.add("subg_color");
    	
    	userDefinedNewColsHtmlType.put("subg_color", "COLOR");
    	userDefinedEditColsHtmlType.put("subg_color", "COLOR");
    	
    	userModifyTD.put("subg_color", "displayColor({subg_color})");
    }
     public String displayColor (HashMap<String,String> hashy) {
    	 return "<td style='background-color:"+hashy.get("subg_color")+";color:black'>"+hashy.get("subg_color")+"</td>";
     }
}
