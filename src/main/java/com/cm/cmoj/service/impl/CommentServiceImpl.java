package com.cm.cmoj.service.impl;

import com.cm.cmoj.mapper.CommentMapper;
import com.cm.cmoj.model.entity.Comment;
import com.cm.cmoj.model.vo.discuss.PageComment;
import com.cm.cmoj.service.CommentService;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description: 博客评论业务层实现
 * @Author: Naccl
 * @Date: 2020-08-03
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId) {
        List<Comment> comments = commentMapper.getListByPageAndParentCommentId(page, blogId, parentCommentId);
        for (Comment c : comments) {
            //递归查询子评论及其子评论
            List<Comment> replyComments = getListByPageAndParentCommentId(page, blogId, c.getId());
            c.setReplyComments(replyComments);
        }
        return comments;
    }



    @Override
    public Comment getCommentById(Long id) {
        Comment comment = commentMapper.getCommentById(id);
        if (comment == null) {
            throw new PersistenceException("评论不存在");
        }
        return comment;
    }

    @Override
    public List<PageComment> getPageCommentList(Long blogId, Long parentCommentId) {
        List<PageComment> comments = getPageCommentListByPageAndParentCommentId(blogId, parentCommentId);
        for (PageComment c : comments) {
            // 递归获取子评论
            processReplies(c);
        }

        return comments;
    }

    // 递归处理每个评论的回复
    private void processReplies(PageComment comment) {
        List<PageComment> replyComments = comment.getReplyComments();
        if (replyComments != null && !replyComments.isEmpty()) {
            // 递归处理每个子评论的回复
            for (PageComment reply : replyComments) {
                processReplies(reply);
            }
            // 排序：按创建时间排序，确保回复按时间顺序排列
            replyComments.sort(Comparator.comparing(PageComment::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    }

    private List<PageComment> getPageCommentListByPageAndParentCommentId(Long blogId, Long parentCommentId) {
        // 查询所有父评论和子评论
        List<PageComment> allComments = commentMapper.getAllCommentsByBlogId(blogId);

        // 创建一个 Map 存储所有评论，便于根据 parentId 查找
        Map<Long, List<PageComment>> commentMap = new HashMap<>();

        // 根据 parentId 分类所有评论
        for (PageComment comment : allComments) {
            commentMap.computeIfAbsent(comment.getParentCommentId(), k -> new ArrayList<>()).add(comment);
        }

        // 根评论 parentCommentId 为 -1，构建树形结构
        return buildCommentTree(commentMap, parentCommentId);
    }

    private List<PageComment> buildCommentTree(Map<Long, List<PageComment>> commentMap, Long parentCommentId) {
        // 获取当前层级的评论
        List<PageComment> comments = commentMap.getOrDefault(parentCommentId, new ArrayList<>());

        // 遍历当前层级的评论
        for (PageComment comment : comments) {
            // 查找父评论
            Long parentId = comment.getParentCommentId();
            if (parentId != null && parentId != -1) {
                // 从 Map 中获取父评论
                List<PageComment> parentList = commentMap.get(parentId);
                if (parentList != null && !parentList.isEmpty()) {
                    // 父评论的昵称
                    comment.setParentCommentNickname(parentList.get(0).getNickname());
                }
            }

            // 构建子评论树
            comment.setReplyComments(buildCommentTree(commentMap, comment.getId()));
        }

        return comments;
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCommentPublishedById(Long commentId, Boolean published) {
        //如果是隐藏评论，则所有子评论都要修改成隐藏状态
        if (!published) {
            List<Comment> comments = getAllReplyComments(commentId);
            for (Comment c : comments) {
                hideComment(c);
            }
        }

        if (commentMapper.updateCommentPublishedById(commentId, published) != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCommentNoticeById(Long commentId, Boolean notice) {
        if (commentMapper.updateCommentNoticeById(commentId, notice) != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCommentById(Long commentId) {
        List<Comment> comments = getAllReplyComments(commentId);
        for (Comment c : comments) {
            delete(c);
        }
        if (commentMapper.deleteCommentById(commentId) != 1) {
            throw new PersistenceException("评论删除失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCommentsByBlogId(Long blogId) {
        commentMapper.deleteCommentsByBlogId(blogId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateComment(Comment comment) {
        if (commentMapper.updateComment(comment) != 1) {
            throw new PersistenceException("评论修改失败");
        }
    }

    @Override
    public int countByPageAndIsPublished(Long blogId, Boolean isPublished) {
        return commentMapper.countByPageAndIsPublished(blogId, isPublished);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(com.cm.cmoj.model.dto.discuss.Comment comment) {
        if (commentMapper.saveComment(comment) != 1) {
            throw new PersistenceException("评论失败");
        }
    }

    /**
     * 递归删除子评论
     *
     * @param comment 需要删除子评论的父评论
     */
    private void delete(Comment comment) {
        for (Comment c : comment.getReplyComments()) {
            delete(c);
        }
        if (commentMapper.deleteCommentById(comment.getId()) != 1) {
            throw new PersistenceException("评论删除失败");
        }
    }

    /**
     * 递归隐藏子评论
     *
     * @param comment 需要隐藏子评论的父评论
     */
    private void hideComment(Comment comment) {
        for (Comment c : comment.getReplyComments()) {
            hideComment(c);
        }
        if (commentMapper.updateCommentPublishedById(comment.getId(), false) != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
     * 按id递归查询子评论
     *
     * @param parentCommentId 需要查询子评论的父评论id
     * @return
     */
    private List<Comment> getAllReplyComments(Long parentCommentId) {
        List<Comment> comments = commentMapper.getListByParentCommentId(parentCommentId);
        for (Comment c : comments) {
            List<Comment> replyComments = getAllReplyComments(c.getId());
            c.setReplyComments(replyComments);
        }
        return comments;
    }
}
