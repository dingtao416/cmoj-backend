package com.cm.cmoj.model.dto.discuss;

import lombok.*;

/**
 * 访问日志备注
 *
 * @author: Naccl
 * @date: 2022-01-08
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitLogRemark {
	/**
	 * 访问内容
	 */
	private String content;

	/**
	 * 备注
	 */
	private String remark;
}
