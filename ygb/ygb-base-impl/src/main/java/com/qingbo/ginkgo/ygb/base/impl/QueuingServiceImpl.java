package com.qingbo.ginkgo.ygb.base.impl;

import org.springframework.stereotype.Service;

import com.qingbo.ginkgo.ygb.base.service.QueuingService;
import com.qingbo.ginkgo.ygb.common.result.Result;
import com.qingbo.ginkgo.ygb.common.util.QueuingMake;

@Service("queuingService")
public class QueuingServiceImpl implements QueuingService {

	public Result<Long> next(String src) {
		Long queuing = QueuingMake.next(src);
		return Result.newSuccess(queuing);
	}

}
