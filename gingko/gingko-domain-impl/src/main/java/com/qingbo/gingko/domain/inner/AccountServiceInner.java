package com.qingbo.gingko.domain.inner;

import com.qingbo.gingko.common.result.Result;

public interface AccountServiceInner {
	Result<Boolean> deposit(Integer accountLogId);
	Result<Boolean> withdraw(Integer accountLogId);
	Result<Boolean> freeze(Integer accountLogId);
	Result<Boolean> unfreeze(Integer accountLogId);
	Result<Boolean> handle(Integer accountLogId);
}
