package com.app.bussframework;

public  class SingleQueueFactory    {
	public SingleQueue getSingleQueuObj(String stageCode, String stepCode) {
		if (stageCode.equalsIgnoreCase("store") && stepCode.equalsIgnoreCase("in_store"))//old not used
			return new SingleQueue_Store_InStore();
		else if (stageCode.equalsIgnoreCase("init") && stepCode.equalsIgnoreCase("instorage"))
			return new SingleQueue_Init_InStorage();
		else if (stageCode.equalsIgnoreCase("dlv_stg") && stepCode.equalsIgnoreCase("with_agent"))
			return new SingleQueue_dlv_stg_with_agent();
		else if (stageCode.equalsIgnoreCase("init") && stepCode.equalsIgnoreCase("prt_manifest"))
			return new SingleQueue_Init_printmanifest();
		else if (stageCode.equalsIgnoreCase("dlv_stg") && stepCode.equalsIgnoreCase("delivered"))
			return new SingleQueue_dlv_stag_delivered();
		else if (stageCode.equalsIgnoreCase("cncl") && stepCode.equalsIgnoreCase("return_to_cust"))
			return new SingleQueue_cncl_return_to_cust();
		else if (stageCode.equalsIgnoreCase("cncl") && stepCode.equalsIgnoreCase("return_onwaytostore"))
			return new SingleQueue_cncl_return_onwaytostore();
		
		else if (stageCode.equalsIgnoreCase("cncl") && stepCode.equalsIgnoreCase("RTN_WITHRCV_AGENT"))
			return new SingleQueue_cncl_return_withrcvagent();
		else if (stageCode.equalsIgnoreCase("init") && stepCode.equalsIgnoreCase("NEW_ONWAY"))
			return new SingleQueue_Init_NEW_ONWAY();
		return new SingleQueueGeneral();
	}
}
