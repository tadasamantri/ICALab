package com.icalab.deadcode;

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArithmeticConstant;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.Constant;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.EqExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LeExpr;
import soot.jimple.LongConstant;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NumericConstant;
import soot.jimple.OrExpr;
import soot.jimple.RealConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.jimple.internal.JimpleLocal;
import vasco.Context;
import vasco.ForwardInterProceduralAnalysis;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;


/**
 * Class implementing non-linear constant propagation with VASCO for dead code implementation
 * 
 * 
 */
public class InterproceduralAnalysis extends
		ForwardInterProceduralAnalysis<SootMethod, Unit, Map<FlowAbstraction,Constant>> {

	private final EmptyReporter reporter;
	public InterproceduralAnalysis(EmptyReporter reporter) {
		super();
		this.reporter = reporter;
	}

	// An artificial local representing returned value of a procedure (used because a method can have multiple return statements).
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", null);
	
	
	/**
	 * If input contains a numeric constant for the Value rhs, output.get(lhs) will contain this numeric constant afterwards.
	 * @param lhs The key to which the value from rhs of the input map will be copied to
	 * @param rhs The key from which the value will be looked up in the input map
	 * @param input The input map which is the lookup map.
	 * @param output The output map which shall be updated appropriate.
	 */
	private void assign(Value lhs, Value rhs, Map<FlowAbstraction, Constant> input, Map<FlowAbstraction, Constant> output) {
		
		// Check if the RHS operand is a numeric constant, local or fieldref.
		FlowAbstraction faLHS = FlowAbstraction.v(lhs);
		if(faLHS!=null)
		if (rhs instanceof Constant) {
			// If RHS is a constant, it is a direct gen
			output.put(faLHS, (Constant) rhs);
		} else if (rhs instanceof Local || rhs instanceof InstanceFieldRef) {
			// Generate new flow abstraction for RHS accordingly, and put value of input to LHS key of output. 
			FlowAbstraction faRHS = FlowAbstraction.v(rhs);
			if(input.containsKey(faRHS)) {
				output.put(faLHS, input.get(faRHS));
			}
		} else if (rhs instanceof BinopExpr){
			// special handling for binopExpr, here we need to actually evaluate the expression
			BinopExpr binop = (BinopExpr) rhs;
			Value op1 = binop.getOp1();
			Value op2 = binop.getOp2();
			Constant c1 = getConstantForValue(input, op1);
			Constant c2 = getConstantForValue(input, op2);
			if(c1 != null && c2 != null){
				Constant v = evaluate(binop, c1, c2);
				output.put(faLHS, v);
			} else{
				output.put(faLHS, null);
			}
		}
		else{
			output.put(faLHS, null);
		}
	}


	private Constant getConstantForValue(Map<FlowAbstraction, Constant> input,
			Value v) {
		Constant c1;
		//if value v is a numeric constant, simply return itself, otherwise do the lookup in the input map.
		if(v instanceof Constant){
			c1 = (Constant) v;
		} else {
			FlowAbstraction fOp1 = FlowAbstraction.v(v);
			c1 = input.get(fOp1);
		}
		return c1;
	}

	
	/**
	 *  This method actually evaluates the binopExpr, here we treat only +,-,*,/,and,or,xor,shiftleft,shiftRight and compare operations will be evaluated. 
	 */
	private Constant evaluate(BinopExpr binop, Constant c1, Constant c2){
	if(c1 instanceof DoubleConstant || c2 instanceof DoubleConstant){
		if (binop instanceof AddExpr)
            return ((DoubleConstant)c1).add((DoubleConstant)c2);
    	else if (binop instanceof SubExpr)
        	return ((DoubleConstant)c1).subtract((DoubleConstant)c2);
        else if (binop instanceof MulExpr)
        	return ((DoubleConstant)c1).multiply((DoubleConstant)c2);
        else if (binop instanceof DivExpr)
    		return ((DoubleConstant)c1).divide((DoubleConstant)c2);
	}
	else{
		if (binop instanceof AddExpr)
            return ((IntConstant)c1).add((IntConstant)c2);
    	else if (binop instanceof SubExpr)
        	return ((IntConstant)c1).subtract((IntConstant)c2);
        else if (binop instanceof MulExpr)
        	return ((IntConstant)c1).multiply((IntConstant)c2);
        else if (binop instanceof DivExpr)
    		return ((IntConstant)c1).divide((IntConstant)c2);
	}
     if(binop instanceof CmpgExpr)
            return ((RealConstant) c1).cmpg((RealConstant) c2);
        else if(binop instanceof CmplExpr)
            return ((RealConstant) c1).cmpl((RealConstant) c2);
        else if(binop instanceof XorExpr)
        	return ((ArithmeticConstant)c1).xor((ArithmeticConstant)c2);
        else if(binop instanceof ShlExpr)
        	return ((ArithmeticConstant)c1).shiftLeft((ArithmeticConstant)c2);
        else if(binop instanceof ShrExpr)
        	return ((ArithmeticConstant)c1).shiftRight((ArithmeticConstant)c2);
        else if(binop instanceof AndExpr)
        	return ((ArithmeticConstant)c1).and((ArithmeticConstant)c2);
        else if(binop instanceof OrExpr)
        	return ((ArithmeticConstant)c1).or((ArithmeticConstant)c2);
        else if(binop instanceof CmpExpr)
        	return ((LongConstant)c1).cmp((LongConstant)c2);
        else if(binop instanceof UshrExpr)
        	return ((ArithmeticConstant)c1).unsignedShiftRight((ArithmeticConstant)c2);
      throw new RuntimeException("Not supported");
	}
	
	@Override
	public Map<FlowAbstraction, Constant> normalFlowFunction(Context<SootMethod, Unit, Map<FlowAbstraction, Constant>> context, Unit unit, Map<FlowAbstraction, Constant> inValue) {
		// Initialize result to input
		Map<FlowAbstraction, Constant> outValue = copy(inValue);
	//At stmt if of type Goto then doing analysis on the target unit	
	  if(unit instanceof GotoStmt)
	  {	
		  unit=((GotoStmt)unit).getTargetBox().getUnit();
	  }			// At stmt which are of type AssignStmt, any previous holding Constant for RHS needs to be copied to LHS value
		
	  if (unit instanceof AssignStmt) {
			// Get operands
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			Value rhsOp = ((AssignStmt) unit).getRightOp();
			assign(lhsOp, rhsOp, inValue, outValue);	
		
			if(lhsOp instanceof Local && rhsOp instanceof Local){
				for(FlowAbstraction abs : inValue.keySet()){
					if(abs.getLocal().equals(rhsOp)){
						FlowAbstraction deriveWithNewLocal = abs.deriveWithNewLocal((Local) lhsOp);
						outValue.put(deriveWithNewLocal, inValue.get(abs));
					}
				}
			}
			
			//Handle return statement values in the Outset
		} else if (unit instanceof ReturnStmt) {
			// Get operand
			Value rhsOp = ((ReturnStmt) unit).getOp();
			assign(RETURN_LOCAL, rhsOp, inValue, outValue);
		}else if (unit instanceof IfStmt){
			IfStmt ifStmt = (IfStmt) unit;
			Boolean result=checkIfConditionHolds(ifStmt, outValue,unit,context);
				
		//Adding to delete if results into a constant value
			if(result!=null){
				//Checking if the ifstmt is already executed and reported 
				if(Driver.ifStmts.containsKey(context.getMethod())){
					Map<Unit,Boolean> del=Driver.ifStmts.get(context.getMethod());
					//Checking if the ifstmt is already executed and reported 
					int set=0;
					if(del.containsKey(unit) && del.get(unit)!=result){
					if(unit.toString().contains("goto (branch)"))
					{
						Unit Target1=ifStmt.getTargetBox().getUnit();
						Unit Target2=null;
						int set1=0;
						for(Map.Entry<Unit, Boolean> entry:del.entrySet()){
							if(entry.getKey()==unit){
								set1=1;
								Target2=((IfStmt)entry.getKey()).getTargetBox().getUnit();
							}
							
							if(set1==1 && Target1==Target2)
								set=1;
						}
					}
					if(set==1){
						del.remove(unit);
						Driver.ifStmts.remove(context.getMethod());
						Driver.ifStmts.put(context.getMethod(), del);
					}
					}
					else
					{
						del.put(unit, result);
						Driver.ifStmts.put(context.getMethod(), del);
					}
				}
				
				else{
					Map<Unit,Boolean> del=new HashMap<Unit,Boolean>();
					del.put(unit, result);
					Driver.ifStmts.put(context.getMethod(), del);
				}
			}
			
		}
		return outValue;
	}
//Checking if condition for constant value
	private Boolean checkIfConditionHolds(IfStmt ifStmt,
			Map<FlowAbstraction, Constant> outValue,Unit unit,Context<SootMethod, Unit, Map<FlowAbstraction, Constant>> context) {
		Value op = ifStmt.getCondition(); 
		Boolean result=null;
		if(op instanceof EqExpr){
			EqExpr eqExpr = (EqExpr) op;
			Constant c1 = getConstantForValue(outValue,eqExpr.getOp1());
			Constant c2 = getConstantForValue(outValue,eqExpr.getOp2());
			if(c1 instanceof IntConstant || c2 instanceof IntConstant)
			if(c1 != null && c2 != null){
			NumericConstant equalEqual = ((NumericConstant)c1).equalEqual
                    ((NumericConstant)c2);
			result=!equalEqual.equals(IntConstant.v(0));
			if(equalEqual.equals(IntConstant.v(0))){
		//		reporter.report(unit, false);
			}
			}
		} else if (op instanceof NeExpr){
			NeExpr neExpr = (NeExpr) op;
			Constant c1 = getConstantForValue(outValue,neExpr.getOp1());
			Constant c2 = getConstantForValue(outValue,neExpr.getOp2());
			if(c1 instanceof IntConstant || c2 instanceof IntConstant)
			if(c1 != null && c2 != null){
				NumericConstant notEqual = ((NumericConstant)c1).notEqual
	                    ((NumericConstant)c2);
				result=!notEqual.equals(IntConstant.v(0));
			//	reporter.report(unit, notEqual.equals(IntConstant.v(0)));
			}
		} else if(op instanceof LeExpr){
			LeExpr expr = (LeExpr) op;
			Constant c1 = getConstantForValue(outValue,expr.getOp1());
			Constant c2 = getConstantForValue(outValue,expr.getOp2());
			if(c1 instanceof IntConstant || c2 instanceof IntConstant)
			if(c1 != null && c2 != null){
				NumericConstant equalEqual = ((NumericConstant)c1).lessThan((NumericConstant)c2);
				result=!equalEqual.equals(IntConstant.v(0));
			//	reporter.report(unit, !equalEqual.equals(IntConstant.v(0)));
			}
		} else if(op instanceof GtExpr){
			GtExpr expr = (GtExpr) op;
			Constant c1 = getConstantForValue(outValue,expr.getOp1());
			Constant c2 = getConstantForValue(outValue,expr.getOp2());
			if(c1 instanceof IntConstant || c2 instanceof IntConstant)
			if(c1 != null && c2 != null){
				NumericConstant equalEqual = ((NumericConstant)c1).greaterThan((NumericConstant)c2);
				result=!equalEqual.equals(IntConstant.v(0));
			//	reporter.report(unit, !equalEqual.equals(IntConstant.v(0)));
			}
		}
		return result;
	}

//Called when a function is called
	@Override
	public Map<FlowAbstraction, Constant> callEntryFlowFunction(Context<SootMethod, Unit, Map<FlowAbstraction, Constant>> context, SootMethod calledMethod, Unit unit, Map<FlowAbstraction, Constant> inValue) {
		// Initialise result to empty map
		Map<FlowAbstraction, Constant> entryValue = topValue();
		if(!calledMethod.hasActiveBody())
			return inValue;
		
		// Map arguments to parameters
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = calledMethod.getActiveBody().getParameterLocal(i);
			assign(param, arg, inValue, entryValue);
		}
		// And instance of the this local
		if (ie instanceof InstanceInvokeExpr) {
			Value instance = ((InstanceInvokeExpr) ie).getBase();
			Local thisLocal = calledMethod.getActiveBody().getThisLocal();
			assign(thisLocal, instance, inValue, entryValue);
		}
	  
		//map incoming fields appropriate
		for(FlowAbstraction key : inValue.keySet()){
			for (int i = 0; i < ie.getArgCount(); i++) {
				Value arg = ie.getArg(i);
				Local param = calledMethod.getActiveBody().getParameterLocal(i);
				if(key.getLocal()!=null)
				if(arg instanceof Local && key.getLocal().equals(arg)){
					FlowAbstraction deriveWithNewLocal = key.deriveWithNewLocal(param);
					entryValue.put(deriveWithNewLocal, inValue.get(key));
				}
			}
			if (ie instanceof InstanceInvokeExpr) {
				Value instance = ((InstanceInvokeExpr) ie).getBase();
				Local thisLocal = calledMethod.getActiveBody().getThisLocal();

				if(key.getLocal()!=null)
				if(instance instanceof Local && key.getLocal().equals(instance)){
					FlowAbstraction deriveWithNewLocal = key.deriveWithNewLocal(thisLocal);
					entryValue.put(deriveWithNewLocal, inValue.get(key));
				}
			}

		}
if(unit instanceof AssignStmt)
		if(((AssignStmt) unit).containsInvokeExpr()){
			InvokeExpr ve=((AssignStmt) unit).getInvokeExpr();
	//Handling Reflection calls
		if(ve.getMethod().getSignature().contains("java.lang.Class: java.lang.reflect.Method getMethod(java.lang.String,java.lang.Class[]") && ve instanceof VirtualInvokeExpr){
		VirtualInvokeExpr ve1=(VirtualInvokeExpr)ve;
				for (int i = 0; i < ve.getArgCount(); i++) {
					Value arg = ve.getArg(i);
					FlowAbstraction lhs= FlowAbstraction.v(arg);
					FlowAbstraction lhs1= FlowAbstraction.v(ve1.getBase());
			if(arg instanceof Constant && inValue.containsKey(lhs1)){
			String className=((ClassConstant)(inValue.get(lhs1))).getValue();
			className=className.replace("/", ".");
				Driver.runmethods.put(className, arg.toString());
			}
			else if((inValue.containsKey(lhs) && inValue.containsKey(lhs1))){
				String className=((ClassConstant)(inValue.get(lhs1))).getValue();
				className=className.replace("/", ".");
					if(inValue.get(lhs)!=null)
					Driver.runmethods.put(className, inValue.get(lhs).toString());
				}
			}		
			
			
		} 
		
		}
		
		return entryValue;
	}
	
	
	@Override
	public Map<FlowAbstraction, Constant> CheckInOutSet(Map<FlowAbstraction, Constant> InSet,Map<FlowAbstraction, Constant> OutSet){
		for(Map.Entry<FlowAbstraction, Constant> entry: InSet.entrySet()){
			if(OutSet.containsKey(entry.getKey())){
				InSet.put(entry.getKey(), OutSet.get(entry.getKey()));
			}
		}
		return InSet;
		
	}
	
	@Override
	public Map<FlowAbstraction, Constant> callExitFlowFunction(Context<SootMethod, Unit, Map<FlowAbstraction, Constant>> context, SootMethod calledMethod, Unit unit, Map<FlowAbstraction, Constant> exitValue) {
		// Initialise result to an empty value
		Map<FlowAbstraction, Constant> afterCallValue = topValue();
		// Only propagate constants for return values
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			assign((Local) lhsOp, RETURN_LOCAL, exitValue, afterCallValue);
		}
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
		for(FlowAbstraction key : exitValue.keySet()){
			for (int i = 0; i < ie.getArgCount(); i++) {
				Value arg = ie.getArg(i);
				Local param = calledMethod.getActiveBody().getParameterLocal(i);
				if(key.getLocal()!=null )
				if(arg instanceof Local && key.getLocal().equals(param)){
					FlowAbstraction deriveWithNewLocal = key.deriveWithNewLocal((Local)arg);
					afterCallValue.put(deriveWithNewLocal, exitValue.get(key));
				}
			}
			// And instance of the this local
			if (ie instanceof InstanceInvokeExpr) {
				Value instance = ((InstanceInvokeExpr) ie).getBase();
				Local thisLocal = calledMethod.getActiveBody().getThisLocal();
				if(key.getLocal()!=null)
				if(instance instanceof Local && key.getLocal().equals(thisLocal)){
					FlowAbstraction deriveWithNewLocal = key.deriveWithNewLocal((Local)instance);
					afterCallValue.put(deriveWithNewLocal, exitValue.get(key));
				}
			}

		}
		// Return the map with the returned value's constant
		return afterCallValue;
	}

	@Override
	public Map<FlowAbstraction, Constant> callLocalFlowFunction(Context<SootMethod, Unit, Map<FlowAbstraction, Constant>> context, Unit unit, Map<FlowAbstraction, Constant> inValue) {
		// Initialise result to the input
		Map<FlowAbstraction, Constant> afterCallValue = topValue();
		// We just need to remove information of the return value.
		for (Map.Entry<FlowAbstraction,Constant> entry : inValue.entrySet())
		 {
			if(entry.getValue()!=null)
				afterCallValue.put(entry.getKey(),entry.getValue());
		 }
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			afterCallValue.remove(lhsOp);
		}
		// Rest of the map remains the same
		return afterCallValue;
		
	}
	
	@Override
	public Map<FlowAbstraction, Constant> boundaryValue(SootMethod method) {
		return topValue();
	}

	@Override
	public Map<FlowAbstraction, Constant> copy(Map<FlowAbstraction, Constant> src) {
		return new HashMap<FlowAbstraction, Constant>(src);
	}



	@Override
	public Map<FlowAbstraction, Constant> meet(Map<FlowAbstraction, Constant> op1, Map<FlowAbstraction, Constant> op2) {
		Map<FlowAbstraction, Constant> result;
		// First add everything in the first operand
		result = new HashMap<FlowAbstraction, Constant>(op1);
		// Then add everything in the second operand, bottoming out the common keys with different values
		for (FlowAbstraction x : op2.keySet()) {
			if (op1.containsKey(x)) {
				// Check the values in both operands
				Constant c1 = op1.get(x);
				Constant c2 = op2.get(x);
				
				if (c1!=null && c1.equals(c2) == false) {
					// Set to non-constant
					result.put(x, null);
				}
			} else {
				// Only in second operand, so add as-is
				result.put(x, op2.get(x));
			}
		}
		return result;
	}

	/**
	 * Returns an empty map.
	 */
	@Override
	public Map<FlowAbstraction, Constant> topValue() {
		return new HashMap<FlowAbstraction, Constant>();
	}

	/**
	 * Returns a default jimple representation.
	 * @see DefaultJimpleRepresentation
	 */
	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}


	
	

	}
