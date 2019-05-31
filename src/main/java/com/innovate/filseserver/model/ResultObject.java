package com.innovate.filseserver.model;

/**
 * 返回对象
 */
public class ResultObject {
	private String result;
	private String message;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 *操作的结果类型
	 */
	public static enum OPERATE_RESULT{
		success,
		fail,
		empty;
		private OPERATE_RESULT(){}
	}
	
}
