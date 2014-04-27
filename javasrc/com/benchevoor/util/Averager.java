package com.benchevoor.util;

public class Averager {

	private final double[] list;
	private int pos = 0;
	private final int maxSize;
	private double total = 0;
	
	/**
	 * 
	 * @param n		the size of the Collector
	 */
	public Averager(int n) {
		this.list = new double[n];
		
		for(int i = 0; i < n; i++) {
			this.list[i] = 0;
		}
		
		this.maxSize = n;
	}
	
	public void add(double energy) {
		this.total -= this.list[this.getPos()];
		
		this.list[this.getPos()] = energy;
		
		this.total += energy;
		
		this.pos++;
	}
	
	public double getAverage() {
		if(this.pos == 0) return 0;
		
		//doesnt count for overflow...fuck it
		if(this.pos > maxSize) {
			return (double) (this.total / (double) maxSize);
		} else {
			return (double) (this.total / (double) this.pos);
		}
		
	}
	
	private int getPos() {
		return this.pos % maxSize;
	}
}
