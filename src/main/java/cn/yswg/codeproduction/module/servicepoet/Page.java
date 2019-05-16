package cn.yswg.codeproduction.module.servicepoet;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer pageNum;
	private Integer pageSize;
	private Integer startRow;
	private Integer endRow;
	private Long total;
	private Long pages;
	private List<T> list;

	public Page() {
	}

	public Page(Integer pageNum, Integer pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
		this.endRow = pageNum * pageSize;
	}

	public Page(Long total, List<T> list) {
		this.total = total;
		this.list = list;
	}

	public Page(Integer total, List<T> list) {
		this.total = (long)total;
		this.list = list;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Long getPages() {
		return pages;
	}

	public void setPages(Long pages) {
		this.pages = pages;
	}

	public Integer getEndRow() {
		return endRow;
	}

	public void setEndRow(Integer endRow) {
		this.endRow = endRow;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Page{" + "pageNum=" + pageNum + ", pageSize=" + pageSize + ", startRow=" + startRow + ", endRow="
				+ endRow + ", total=" + total + ", pages=" + pages + '}';
	}
}
