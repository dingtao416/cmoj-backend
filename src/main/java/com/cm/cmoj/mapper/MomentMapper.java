package com.cm.cmoj.mapper;

import com.cm.cmoj.model.entity.Moment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: 博客动态持久层接口
 * @Author: Naccl
 * @Date: 2020-08-24
 */
@Mapper
public interface MomentMapper {
	List<Moment> getMomentList();

	int addLikeByMomentId(Long momentId);

	int updateMomentPublishedById(Long momentId, Boolean published);

	Moment getMomentById(Long id);

	int deleteMomentById(Long id);

	int saveMoment(Moment moment);

	int updateMoment(Moment moment);
}
