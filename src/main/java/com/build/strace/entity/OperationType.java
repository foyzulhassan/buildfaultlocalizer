package com.build.strace.entity;

public enum OperationType {
	Open(1),
	Read(2),
	Write(3),
	Close(4);
	
	private int operationtype;

	OperationType(int op) {
		this.operationtype = op;
	}

	public int getOperation() {
		return this.operationtype;
	}
}
