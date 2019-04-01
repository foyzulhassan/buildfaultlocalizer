package com.build.strace.entity;

import java.util.ArrayList;
import java.util.List;

public class Entry {
	private double tstamp;	
	private String result;
	private String func;
	private List<String> args;
	
	public Entry()
	{
		args=new ArrayList<>();
	}
	
	public Entry(double tstamp,String result, String func,List<String> args)
	{
		this.tstamp=tstamp;
		this.result=result;
		this.func=func;
		this.args=args;
	}
	
	public double getTstamp() {
		return tstamp;
	}
	public void setTstamp(double tstamp) {
		this.tstamp = tstamp;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public List<String> getArgs() {
		return args;
	}
	public void setArgs(List<String> args) {
		this.args = args;
	}
	
	public void addToArgs(String arg)
	{
		this.args.add(arg);
	}
}
