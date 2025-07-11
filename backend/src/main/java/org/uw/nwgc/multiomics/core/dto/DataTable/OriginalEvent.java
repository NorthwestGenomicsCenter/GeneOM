package org.uw.nwgc.multiomics.core.dto.DataTable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OriginalEvent{

	@JsonProperty("pageCount")
	private int pageCount;

	@JsonProperty("page")
	private int page;

	@JsonProperty("rows")
	private int rows;

	@JsonProperty("first")
	private int first;

	public void setPageCount(int pageCount){
		this.pageCount = pageCount;
	}

	public int getPageCount(){
		return pageCount;
	}

	public void setPage(int page){
		this.page = page;
	}

	public int getPage(){
		return page;
	}

	public void setRows(int rows){
		this.rows = rows;
	}

	public int getRows(){
		return rows;
	}

	public void setFirst(int first){
		this.first = first;
	}

	public int getFirst(){
		return first;
	}

	@Override
 	public String toString(){
		return 
			"OriginalEvent{" + 
			"pageCount = '" + pageCount + '\'' + 
			",page = '" + page + '\'' + 
			",rows = '" + rows + '\'' + 
			",first = '" + first + '\'' + 
			"}";
		}
}