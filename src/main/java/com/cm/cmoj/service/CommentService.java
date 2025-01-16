package com.cm.cmoj.service;


import com.cm.cmoj.model.entity.Comment;
import com.cm.cmoj.model.vo.discuss.PageComment;

import java.util.List;

public interface CommentService {
	List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	List<PageComment> getPageCommentList( Long blogId, Long parentCommentId);

	Comment getCommentById(Long id);

	void updateCommentPublishedById(Long commentId, Boolean published);

	void updateCommentNoticeById(Long commentId, Boolean notice);

	void deleteCommentById(Long commentId);

	void deleteCommentsByBlogId(Long blogId);

	void updateComment(Comment comment);

	int countByPageAndIsPublished(Long blogId, Boolean isPublished);

	void saveComment(com.cm.cmoj.model.dto.discuss.Comment comment);
}
