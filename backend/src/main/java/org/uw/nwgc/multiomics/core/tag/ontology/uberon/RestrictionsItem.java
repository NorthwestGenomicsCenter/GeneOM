package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestrictionsItem{

	@JsonProperty("fillerId")
	private String fillerId;

	@JsonProperty("propertyId")
	private String propertyId;

	public void setFillerId(String fillerId){
		this.fillerId = fillerId;
	}

	public String getFillerId(){
		return fillerId;
	}

	public void setPropertyId(String propertyId){
		this.propertyId = propertyId;
	}

	public String getPropertyId(){
		return propertyId;
	}

	@Override
 	public String toString(){
		return 
			"RestrictionsItem{" + 
			"fillerId = '" + fillerId + '\'' + 
			",propertyId = '" + propertyId + '\'' + 
			"}";
		}
}