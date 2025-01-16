package com.cm.cmoj.utils.comment.channel;

import com.cm.cmoj.config.discuss.properties.BlogProperties;
import com.cm.cmoj.config.discuss.properties.TelegramProperties;
import com.cm.cmoj.model.dto.discuss.Comment;
import com.cm.cmoj.model.enums.CommentPageEnum;
import com.cm.cmoj.utils.StringUtils;
import com.cm.cmoj.utils.comment.CommentUtils;
import com.cm.cmoj.utils.telegram.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * Telegram提醒方式
 *
 * @author: Naccl
 * @date: 2022-01-22
 */
@Slf4j
@Lazy
@Component
public class TelegramChannel implements CommentNotifyChannel {
	private TelegramUtils telegramUtils;

	private BlogProperties blogProperties;

	private TelegramProperties telegramProperties;

	private SimpleDateFormat simpleDateFormat;

	public TelegramChannel(TelegramUtils telegramUtils, BlogProperties blogProperties, TelegramProperties telegramProperties) {
		this.telegramUtils = telegramUtils;
		this.blogProperties = blogProperties;
		this.telegramProperties = telegramProperties;

		this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

		log.info("TelegramChannel instantiating");
		telegramUtils.setWebhook();
	}

	/**
	 * 发送Telegram消息提醒我自己
	 *
	 * @param comment 当前收到的评论
	 */
	@Override
	public void notifyMyself(Comment comment) {
		String url = telegramProperties.getApi() + telegramProperties.getToken() + TelegramUtils.SEND_MESSAGE;
		String content = getContent(comment);
		Map<String, Object> messageBody = telegramUtils.getMessageBody(content);
		telegramUtils.sendByAutoCheckReverseProxy(url, messageBody);
	}

	private String getContent(Comment comment) {
		CommentPageEnum commentPageEnum = CommentUtils.getCommentPageEnum(comment);
		return String.format(
				"<b>您的文章<a href=\"%s\">《%s》</a>有了新的评论~</b>\n" +
						"\n" +
						"<b>%s</b> 给您的评论：\n" +
						"\n" +
						"<pre>%s</pre>\n" +
						"\n" +
						"<b>其他信息：</b>\n" +
						"评论ID：<code>%d</code>\n" +
						"IP：%s\n" +
						"%s" +
						"时间：<u>%s</u>\n" +
						"邮箱：<code>%s</code>\n" +
						"%s" +
						"状态：%s [<a href=\"%s\">管理评论</a>]\n",
				blogProperties.getView() + commentPageEnum.getPath(),
				commentPageEnum.getTitle(),
				comment.getNickname(),
				comment.getContent(),
				comment.getId(),
				comment.getIp(),
				simpleDateFormat.format(comment.getCreateTime()),
				StringUtils.isEmpty(comment.getWebsite()) ? "" : "网站：" + comment.getWebsite() + "\n",
				comment.getPublished() ? "公开" : "待审核",
				blogProperties.getCms() + "/blog/comment/list"
		);
	}
}
