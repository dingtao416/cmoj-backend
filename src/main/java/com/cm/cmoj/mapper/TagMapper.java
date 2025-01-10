package com.cm.cmoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.cmoj.model.entity.Tag;
import com.cm.cmoj.model.vo.discuss.TagBlogCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: 博客标签持久层接口
 * @Author: Naccl
 * @Date: 2020-07-30
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
	List<Tag> getTagList();

	List<Tag> getTagListNotId();

	List<Tag> getTagListByBlogId(Long blogId);

	int saveTag(Tag tag);

	Tag getTagById(Long id);

	Tag getTagByName(String name);

	int deleteTagById(Long id);

	int updateTag(Tag tag);

	List<TagBlogCount> getTagBlogCount();
}
