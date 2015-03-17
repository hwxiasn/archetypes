package com.qingbo.ginkgo.ygb.cms.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.cms.entity.ArticleText;

public interface ArticleTextRepository extends BaseRepository<ArticleText> {
	/**
	 * 通过文章编号获取文章内容
	 */
	ArticleText findByArticleId(Long articleId);
}
