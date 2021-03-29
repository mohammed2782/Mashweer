package com.app.core.html;


import java.util.HashMap;
import java.util.LinkedHashMap;

public class HtmlInputConfig {
	private boolean multiEdit;
	private boolean required;
	private boolean hidden;
	private boolean disabled_attr;
	
	private String BGcolor;
	
	private HashMap<String,String> userDefinedMinDateSelect;
	private HashMap<String,String> userDefinedMaxDateSelect;
	
	
	
	public HashMap<String, String> getUserDefinedMaxDateSelect() {
		return userDefinedMaxDateSelect;
	}
	public void setUserDefinedMaxDateSelect(HashMap<String, String> userDefinedMaxDateSelect) {
		this.userDefinedMaxDateSelect = userDefinedMaxDateSelect;
	}
	/**
	 * @return the userDefinedMinDateSelect
	 */
	public HashMap<String,String> getUserDefinedMinDateSelect() {
		return userDefinedMinDateSelect;
	}
	/**
	 * @param userDefinedMinDateSelect the userDefinedMinDateSelect to set
	 */
	public void setUserDefinedMinDateSelect(HashMap<String,String> userDefinedMinDateSelect) {
		this.userDefinedMinDateSelect = userDefinedMinDateSelect;
	}
	/**
	 * @return the multiEdit
	 */
	public boolean isMultiEdit() {
		return multiEdit;
	}
	/**
	 * @param multiEdit the multiEdit to set
	 */
	public void setMultiEdit(boolean multiEdit) {
		this.multiEdit = multiEdit;
	}
	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	/**
	 * @return the disabled_attr
	 */
	public boolean isDisabled_attr() {
		return disabled_attr;
	}
	/**
	 * @param disabled_attr the disabled_attr to set
	 */
	public void setDisabled_attr(boolean disabled_attr) {
		this.disabled_attr = disabled_attr;
	}
	/**
	 * @return the bGcolor
	 */
	public String getBGcolor() {
		return BGcolor;
	}
	/**
	 * @param bGcolor the bGcolor to set
	 */
	public void setBGcolor(String bGcolor) {
		BGcolor = bGcolor;
	}
}
