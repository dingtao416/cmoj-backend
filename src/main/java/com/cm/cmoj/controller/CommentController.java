package com.cm.cmoj.controller;

import com.cm.cmoj.annotation.AccessLimit;
import com.cm.cmoj.model.dto.discuss.Comment;
import com.cm.cmoj.model.entity.User;
import com.cm.cmoj.model.vo.discuss.PageComment;
import com.cm.cmoj.model.vo.discuss.PageResult;
import com.cm.cmoj.model.vo.discuss.Result;
import com.cm.cmoj.service.BlogService;
import com.cm.cmoj.service.CommentService;
import com.cm.cmoj.service.impl.UserServiceImpl;
import com.cm.cmoj.utils.StringUtils;
import com.cm.cmoj.utils.comment.CommentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 评论
 * @Author: Naccl
 * @Date: 2020-08-15
 */
@RestController
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    BlogService blogService;
    @Autowired
    CommentUtils commentUtils;

    /**
     * 根据页面分页查询评论列表
     *
     *
     * @param blogId   如果page==0，需要博客id参数
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/comments")
    public Result comments(@RequestParam(defaultValue = "") Long blogId, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        //查询该页面所有根评论的数量
        List<PageComment> pageCommentList = commentService.getPageCommentList(blogId, -1L);
        // 计算分页范围
        // 总根评论数
        int totalRootComments = pageCommentList.size();
        int fromIndex = (pageNum - 1) * pageSize; // 开始索引
        int toIndex = Math.min(fromIndex + pageSize, totalRootComments); // 结束索引，防止越界
        // 检查分页范围是否合法
        if (fromIndex >= totalRootComments || fromIndex < 0) {
            return Result.error("页码超出范围");
        }
        // 截取目标页的根评论
        List<PageComment> pagedRootComments = pageCommentList.subList(fromIndex, toIndex);
        // 封装分页结果
        PageResult<PageComment> pageResult = new PageResult<>((int) Math.ceil((double) totalRootComments / pageSize), pagedRootComments);
        Map<String, Object> map = new HashMap<>(8);
        map.put("allComment", totalRootComments );
        map.put("comments", pageResult);
        return Result.ok("获取成功", map);
    }

    /**
     * 单个ip，30秒内允许提交1次评论
     *
     * @param comment 评论DTO
     * @param request 获取ip
     * @return
     */
    @AccessLimit(seconds = 30, maxCount = 1, msg = "30秒内只能提交一次评论")
    @PostMapping("/comment")
    public Result postComment(@RequestBody Comment comment, HttpServletRequest request) {
        //评论内容合法性校验
        if (StringUtils.isEmpty(comment.getContent()) || comment.getContent().length() > 250 || comment.getParentCommentId() == null) {
            return Result.error("参数有误");
        }
        //父评论
        com.cm.cmoj.model.entity.Comment parentComment = null;
        //对于有指定父评论的评论，以父评论为准
        if (comment.getParentCommentId() != -1) {
            //当前评论为子评论
            parentComment = commentService.getCommentById(comment.getParentCommentId());
            Long blogId = parentComment.getBlog().getId();
            comment.setBlogId(blogId);
        }
        //获取评论用户身份
        User commentUser = userService.getLoginUser(request);
        //获取评论 讨论的博主身份
        Long userId = blogService.getBlogById(comment.getBlogId()).getUserId();
        User user = userService.findUserById(userId);
        //博主评论，根据博主信息设置评论属性
        if (commentUser.getId().equals(userId)) {
            commentUtils.setAdminComment(comment, request, user);
        } else {
            commentUtils.setVisitorComment(comment, request,commentUser);
        }
        commentService.saveComment(comment);
        //commentUtils.judgeSendNotify(comment, isVisitorComment, parentComment);
        return Result.ok("评论成功",comment);
    }
}