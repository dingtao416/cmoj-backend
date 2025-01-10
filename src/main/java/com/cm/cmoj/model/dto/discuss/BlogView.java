package com.cm.cmoj.model.dto.discuss;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 博客浏览量
 * @Author: Naccl
 * @Date: 2020-10-06
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogView {
	private Long id;
	private Integer views;
}
