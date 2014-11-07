package com.qingbo.gingko.domain.inner;

import com.qingbo.gingko.common.result.Result;

public interface AccountServiceInner {
	Result<Boolean> deposit(Long accountLogId);
	Result<Boolean> withdraw(Long accountLogId);
	Result<Boolean> freeze(Long accountLogId);
	Result<Boolean> unfreeze(Long accountLogId);
	Result<Boolean> handle(Long accountLogId);
}
