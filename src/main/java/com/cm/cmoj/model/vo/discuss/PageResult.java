package com.cm.cmoj.model.vo.discuss;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 分页结果
 * @Author: Naccl
 * @Date: 2020-08-08
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PageResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer totalPage;//总页数
	private List<T> list;//数据

	public PageResult(Integer totalPage, List<T> list) {
		this.totalPage = totalPage;
		this.list = list;
	}
}
