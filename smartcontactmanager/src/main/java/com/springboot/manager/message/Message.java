package com.springboot.manager.message;

public class Message {
	
	private String Message;
	private String type;
	@Override
	public String toString() {
		return "Message [Message=" + Message + ", type=" + type + "]";
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Message(String message, String type) {
		super();
		Message = message;
		this.type = type;
	}
	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
