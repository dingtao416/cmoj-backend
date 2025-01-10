package com.cm.cmoj.model.dto.discuss;

import lombok.*;

/**
 * @Description: UserAgent解析DTO
 * @Author: Naccl
 * @Date: 2022-10-13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserAgentDTO {
	private String os;
	private String browser;
}
