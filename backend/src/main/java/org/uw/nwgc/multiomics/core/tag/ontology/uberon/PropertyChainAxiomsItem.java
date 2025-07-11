package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyChainAxiomsItem{

	@JsonProperty("predicateId")
	private String predicateId;

	@JsonProperty("chainPredicateIds")
	private List<String> chainPredicateIds;

	public void setPredicateId(String predicateId){
		this.predicateId = predicateId;
	}

	public String getPredicateId(){
		return predicateId;
	}

	public void setChainPredicateIds(List<String> chainPredicateIds){
		this.chainPredicateIds = chainPredicateIds;
	}

	public List<String> getChainPredicateIds(){
		return chainPredicateIds;
	}

	@Override
 	public String toString(){
		return 
			"PropertyChainAxiomsItem{" + 
			"predicateId = '" + predicateId + '\'' + 
			",chainPredicateIds = '" + chainPredicateIds + '\'' + 
			"}";
		}
}