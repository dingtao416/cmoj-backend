package com.cm.cmoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.cmoj.model.entity.Comment;
import com.cm.cmoj.model.vo.discuss.PageComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: 博客评论持久层接口
 * @Author: Naccl
 * @Date: 2020-08-03
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
	List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	List<Comment> getListByParentCommentId(Long parentCommentId);

	List<PageComment> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	Comment getCommentById(Long id);

	int updateCommentPublishedById(Long commentId, Boolean published);

	int updateCommentNoticeById(Long commentId, Boolean notice);

	int deleteCommentById(Long commentId);

	int deleteCommentsByBlogId(Long blogId);

	int updateComment(Comment comment);

	int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

	int countComment();

	int saveComment(com.cm.cmoj.model.dto.discuss.Comment comment);
}
