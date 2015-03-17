package com.qingbo.ginkgo.ygb.cms.repository;

import com.qingbo.ginkgo.ygb.base.repository.BaseRepository;
import com.qingbo.ginkgo.ygb.cms.entity.BgmPlatform;

public interface BgmPlatformRepository extends BaseRepository<BgmPlatform> {

	/**
	 * 通过平台标识获取平台
	 */
	BgmPlatform findByTag(String tag);
}
