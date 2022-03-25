package com.parameter.result;

/**
 * 
 * @description: 返回结果实体类
 * @author: xiaodong
 * @date: 2019年2月24日
 *
 */
public class Result {
	
	private Integer code;
	
	private String message;
	
	private Object data;

	/**
	 * 
	 */
	public Result() {
		super();
	}
	

	/**
	 * @param code
	 * @param message
	 */
	public Result(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	/**
	 * @param code
	 * @param message
	 * @param data
	 */
	public Result(Integer code, String message, Object data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}
	
	

}
