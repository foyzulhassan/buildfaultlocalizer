package com.build.strace.entity;

public class OutputEntry {
	public double msgWriteTime;
	public String strMsg;
	
	public OutputEntry(double writetime,String msg)
	{
		this.msgWriteTime=writetime;
		this.strMsg=msg;
	}
	
	public double getMsgWriteTime() {
		return msgWriteTime;
	}

	public void setMsgWriteTime(double msgWriteTime) {
		this.msgWriteTime = msgWriteTime;
	}

	public String getStrMsg() {
		return strMsg;
	}

	public void setStrMsg(String strMsg) {
		this.strMsg = strMsg;
	}

}
