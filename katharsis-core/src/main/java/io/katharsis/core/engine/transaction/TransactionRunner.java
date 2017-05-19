package io.katharsis.core.engine.transaction;

import java.util.concurrent.Callable;

public interface TransactionRunner {

	public <T> T doInTransaction(Callable<T> callable);
}
