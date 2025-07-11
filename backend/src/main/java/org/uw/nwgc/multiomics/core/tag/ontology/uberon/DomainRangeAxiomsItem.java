package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DomainRangeAxiomsItem{

	@JsonProperty("predicateId")
	private String predicateId;

	@JsonProperty("rangeClassIds")
	private List<String> rangeClassIds;

	@JsonProperty("domainClassIds")
	private List<String> domainClassIds;

	public void setPredicateId(String predicateId){
		this.predicateId = predicateId;
	}

	public String getPredicateId(){
		return predicateId;
	}

	public void setRangeClassIds(List<String> rangeClassIds){
		this.rangeClassIds = rangeClassIds;
	}

	public List<String> getRangeClassIds(){
		return rangeClassIds;
	}

	public void setDomainClassIds(List<String> domainClassIds){
		this.domainClassIds = domainClassIds;
	}

	public List<String> getDomainClassIds(){
		return domainClassIds;
	}

	@Override
 	public String toString(){
		return 
			"DomainRangeAxiomsItem{" + 
			"predicateId = '" + predicateId + '\'' + 
			",rangeClassIds = '" + rangeClassIds + '\'' + 
			",domainClassIds = '" + domainClassIds + '\'' + 
			"}";
		}
}