package com.cm.cmoj.controller;

import com.cm.cmoj.model.entity.Tag;
import com.cm.cmoj.model.vo.discuss.*;
import com.cm.cmoj.service.BlogService;
import com.cm.cmoj.service.CommentService;
import com.cm.cmoj.service.TagService;
import com.cm.cmoj.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 博客相关
 * @Author: Naccl
 * @Date: 2020-08-12
 */
@RestController
public class BlogController {
	@Resource
	private BlogService blogService;
    @Resource
	private TagService tagService;
	@Autowired
	CommentService commentService;

	/**
	 * 按置顶、创建时间排序 分页查询博客简要信息列表
	 *
	 * @param pageNum 页码
	 * @return
	 */
	
	@GetMapping("/blogs")
	public Result blogs(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam String categoryId,
						@RequestParam String title,@RequestParam String userId) {
		PageResult<BlogInfo> pageResult = blogService.getBlogInfoListByIsPublished(pageNum,categoryId,title,userId);
		return Result.ok("请求成功", pageResult);
	}

	/**
	 * 按id获取公开博客详情
	 *
	 * @param id  博客id
	 * @return
	 */
	
	@GetMapping("/blog")
	public Result getBlog(@RequestParam Long id) {
		BlogDetail blog = blogService.getBlogByIdAndIsPublished(id);
		return Result.ok("获取成功", blog);
	}

	/**
	 * 按关键字根据文章内容搜索公开且无密码保护的博客文章
	 *
	 * @param query 关键字字符串
	 * @return
	 */

	@GetMapping("/searchBlog")
	public Result searchBlog(@RequestParam String query) {
		//校验关键字字符串合法性
		if (StringUtils.isEmpty(query) || StringUtils.hasSpecialChar(query) || query.trim().length() > 20) {
			return Result.error("参数错误");
		}
		List<SearchBlog> searchBlogs = blogService.getSearchBlogListByQueryAndIsPublished(query.trim());
		return Result.ok("获取成功", searchBlogs);
	}
	@PostMapping("/blog")
	public Result saveBlog(@RequestBody com.cm.cmoj.model.dto.discuss.Blog blog) {
		return getResult(blog, "save");
	}
	@DeleteMapping("/blog")
	public Result delete(@RequestParam Long id) {
		blogService.deleteBlogTagByBlogId(id);
		blogService.deleteBlogById(id);
		commentService.deleteCommentsByBlogId(id);
		return Result.ok("删除成功");
	}

	/**
	 * 更新博客
	 *
	 * @param blog 博客文章DTO
	 * @return
	 */
	@PutMapping("/blog")
	public Result updateBlog(@RequestBody com.cm.cmoj.model.dto.discuss.Blog blog) {
		return getResult(blog, "update");
	}
	private Result getResult(com.cm.cmoj.model.dto.discuss.Blog blog, String type) {
		//验证普通字段
		if (StringUtils.isEmpty(blog.getTitle(), blog.getFirstPicture(), blog.getContent(), blog.getDescription())
				|| blog.getWords() == null || blog.getWords() < 0) {
			return Result.error("参数有误");
		}
		//处理标签
		List<Object> tagList = blog.getTagList();
		List<Tag> tags = new ArrayList<>();
		for (Object t : tagList) {
			if (t instanceof Integer) {//选择了已存在的标签
				Tag tag = tagService.getTagById(((Integer) t).longValue());
				tags.add(tag);
			} else if (t instanceof String) {//添加新标签
				//查询标签是否已存在
				Tag tag1 = tagService.getTagByName((String) t);
				if (tag1 != null) {
					return Result.error("不可添加已存在的标签");
				}
				Tag tag = new Tag();
				tag.setName((String) t);
				tagService.saveTag(tag);
				tags.add(tag);
			} else {
				return Result.error("标签不正确");
			}
		}

		Date date = new Date();
		if (blog.getReadTime() == null || blog.getReadTime() < 0) {
			blog.setReadTime((int) Math.round(blog.getWords() / 200.0));//粗略计算阅读时长
		}
		if (blog.getViews() == null || blog.getViews() < 0) {
			blog.setViews(0);
		}
		if ("save".equals(type)) {
			blog.setCreateTime(date);
			blog.setUpdateTime(date);
			blog.setUserId(blog.getUserId());
			blogService.saveBlog(blog);
			//关联博客和标签(维护 blog_tag 表)
			for (Tag t : tags) {
				blogService.saveBlogTag(blog.getId(), t.getId());
			}
			return Result.ok("添加成功");
		} else {
			blog.setUpdateTime(date);
			blogService.updateBlog(blog);
			//关联博客和标签(维护 blog_tag 表)
			blogService.deleteBlogTagByBlogId(blog.getId());
			for (Tag t : tags) {
				blogService.saveBlogTag(blog.getId(), t.getId());
			}
			return Result.ok("更新成功");
		}
	}
}
