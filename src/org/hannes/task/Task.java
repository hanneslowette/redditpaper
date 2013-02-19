package org.hannes.task;

import java.util.concurrent.Callable;

public abstract class Task<T> implements Callable<T> {

	@Override
	public T call() throws Exception {
		try {
			T t = execute();
			complete(t);
			return t;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * Call on execution
	 * @return
	 */
	public abstract T execute() throws Exception;

	/**
	 * Called on completion
	 * 
	 * @param object
	 */
	public abstract void complete(T object) throws Exception;

}