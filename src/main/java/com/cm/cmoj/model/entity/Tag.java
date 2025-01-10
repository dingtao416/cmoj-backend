package com.cm.cmoj.model.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Tag {
	private Long id;
	private String name;//标签名称
	private String color;//标签颜色(与Semantic UI提供的颜色对应，可选)
	private List<Blog> blogs = new ArrayList<>();//该标签下的博客文章
}
