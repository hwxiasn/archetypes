package com.qingbo.gingko.domain.inner;

import com.qingbo.gingko.common.result.Result;

public interface AccountLogServiceInner {
	Result<Boolean> deposit(Long transactionId);
	Result<Boolean> withdraw(Long transactionId);
	Result<Boolean> transfer(Long transactionId);
	Result<Boolean> freeze(Long transactionId);
	Result<Boolean> unfreeze(Long transactionId);
}
