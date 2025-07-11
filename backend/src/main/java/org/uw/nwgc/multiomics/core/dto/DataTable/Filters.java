package org.uw.nwgc.multiomics.core.dto.DataTable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Filters{

	@JsonProperty("name")
	private Name name;

	public void setName(Name name){
		this.name = name;
	}

	public Name getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"Filters{" + 
			"name = '" + name + '\'' + 
			"}";
		}
}