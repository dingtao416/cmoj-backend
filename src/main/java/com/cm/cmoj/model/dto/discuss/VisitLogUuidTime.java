package com.cm.cmoj.model.dto.discuss;

import lombok.*;

import java.util.Date;

/**
 * @Description: 访客更新DTO
 * @Author: Naccl
 * @Date: 2021-02-05
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitLogUuidTime {
	private String uuid;
	private Date time;
	private Integer pv;
}
