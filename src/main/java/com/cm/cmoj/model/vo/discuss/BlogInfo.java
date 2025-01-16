package com.cm.cmoj.model.vo.discuss;

import com.cm.cmoj.model.entity.Category;
import com.cm.cmoj.model.entity.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 博客简要信息
 * @Author: Naccl
 * @Date: 2020-08-08
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String title;//文章标题
	private String description;//描述
	private Date createTime;//创建时间
	private Integer views;//浏览次数
	private Integer words;//文章字数
	private Integer readTime;//阅读时长(分钟)
	private Boolean top; //是否置顶
	private String password; //文章密码
	private Boolean privacy; //是否私密文章
	private Category category; //文章分类
	private Long userId; //用户id
	private  String userAvatar;
	private  String firstPicture;
	private List<Tag> tags = new ArrayList<>();//文章标签
}
