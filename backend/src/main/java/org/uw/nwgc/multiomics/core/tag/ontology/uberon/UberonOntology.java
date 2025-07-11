package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UberonOntology{

	@JsonProperty("graphs")
	private List<GraphsItem> graphs;

	public void setGraphs(List<GraphsItem> graphs){
		this.graphs = graphs;
	}

	public List<GraphsItem> getGraphs(){
		return graphs;
	}

	@Override
 	public String toString(){
		return 
			"UberonOntology{" + 
			"graphs = '" + graphs + '\'' + 
			"}";
		}
}