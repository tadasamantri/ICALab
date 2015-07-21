package com.icalab.deadcode;

import soot.Local;
import soot.SootField;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JimpleLocal;


/**
 * Class containing the information to be aggregated during the analysis
 * 
 * @author Steven Arzt
 */
public class FlowAbstraction {

	private static FlowAbstraction zeroAbstraction = null;
	private final Local local;
	private final SootField field;
	
	public FlowAbstraction(Local local) {
		this(local, null);
	}
	
	public FlowAbstraction(Local local, SootField field) {
		this.local = local;
		this.field = field;
	}


	public Local getLocal() {
		return this.local;
	}

	public SootField getField() {
		return this.field;
	}

	@Override
	public int hashCode() {
		if (this == zeroAbstraction)
			return 0;
		
		final int prime = 31;
		int result = 1;
		result = prime * result + (local == null ? 0 : local.hashCode());
		result = prime * result + (field == null ? 0 : field.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == zeroAbstraction)
			return obj == zeroAbstraction;
		
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof FlowAbstraction))
			return false;
		FlowAbstraction other = (FlowAbstraction) obj;
	
		if (local == null) {
			if (other.local != null)
				return false;
		} else if (!local.equals(other.local))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String res = "";
		if (local != null)
			res += local;// + " n: "+  local.getNumber();
		if (field != null) {
			if (!res.isEmpty())
				res += ".";
			res += field.getName();
		}
		return res;
	}
	
	
	public FlowAbstraction deriveWithNewLocal(Local local) {
		if (local == null)
			throw new RuntimeException("Target local may not be null");
		
		return new FlowAbstraction(local, field);
	}

	public static FlowAbstraction v(Value v) {
		if (v instanceof Local)
			return new FlowAbstraction((Local) v);
		else if (v instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) v;
			return new FlowAbstraction((Local) ifr.getBase(), ifr.getField());
		}
		else if (v instanceof StaticFieldRef) {
			StaticFieldRef sfr = (StaticFieldRef) v;
			return new FlowAbstraction(null, sfr.getField());
		}else
		if (v instanceof JimpleLocal)
			return new FlowAbstraction((Local) v);
		else {
			return null;////added line by us for handling nullconstants and other soot types
		}
		//throw new RuntimeException("Unexpected left side");
	}
	
	public static FlowAbstraction zeroAbstraction() {
		if (zeroAbstraction != null)
			return zeroAbstraction;
		
		zeroAbstraction	= new FlowAbstraction(null, null);
		return zeroAbstraction;
	}

}
