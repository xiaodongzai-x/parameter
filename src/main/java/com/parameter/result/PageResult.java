
package com.parameter.result;
import java.util.List;

/**
 * 
 * @description: 分页数据实体类
 * @author: xiaodong
 * @date: 2019年2月24日
 *
 */
public class PageResult<T> {
	
	private Long total;
	
	private Integer pages;
	
	private List<T> rows;

	public PageResult() {
		super();
	}

	public PageResult(Long total,Integer pages, List<T> rows) {
		super();
		
		this.total = total;
		this.pages = pages;
		this.rows = rows;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}


	public List<T> getRows() {
		return rows;
	}


	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}
	

}
