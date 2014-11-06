package com.qingbo.gingko.domain.inner;

import com.qingbo.gingko.common.result.Result;

public interface AccountLogServiceInner {
	Result<Boolean> deposit(Integer transactionId);
	Result<Boolean> withdraw(Integer transactionId);
	Result<Boolean> transfer(Integer transactionId);
	Result<Boolean> freeze(Integer transactionId);
	Result<Boolean> unfreeze(Integer transactionId);
}
