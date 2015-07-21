package com.icalab.deadcode;

import soot.Unit;

/**
 * Empty reporter class stub
 * 
 * @author Steven Arzt
 */
public class EmptyReporter {

	public void report(Unit stmt, boolean unreachableCondition) {
		System.out.println("At Statement: " + stmt);
		System.out.println("\tbranch condition: " + unreachableCondition);
		System.out.println("\tcan never be fulfilled!");
	}
}
