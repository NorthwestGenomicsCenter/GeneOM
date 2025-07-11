package org.uw.nwgc.multiomics.core.dto.DataTable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Name{

	@JsonProperty("value")
	private Object value;

	@JsonProperty("matchMode")
	private String matchMode;

	public void setValue(Object value){
		this.value = value;
	}

	public Object getValue(){
		return value;
	}

	public void setMatchMode(String matchMode){
		this.matchMode = matchMode;
	}

	public String getMatchMode(){
		return matchMode;
	}

	@Override
 	public String toString(){
		return 
			"Name{" + 
			"value = '" + value + '\'' + 
			",matchMode = '" + matchMode + '\'' + 
			"}";
		}
}