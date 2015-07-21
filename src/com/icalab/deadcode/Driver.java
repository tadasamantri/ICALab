package com.icalab.deadcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import soot.Body;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.Singletons.Global;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.options.Options;
import soot.util.Chain;

public class Driver extends Options{
	public Driver(Global g) {
		super(g);
		// TODO Auto-generated constructor stub
	}
	public static boolean j_Unit_Boolean = false;
	public static Map<SootMethod, Map<Unit, Boolean>> ifStmts=new  HashMap<SootMethod,Map<Unit,Boolean>>();
	public static Map<SootMethod, Map<Unit, Boolean>> AllifStmts=new  HashMap<SootMethod,Map<Unit,Boolean>>();
	public static CallGraph cg;
	public static Map<String,String> runmethods=new  HashMap<String,String>();
	private static DotGraph dg = new DotGraph("CallGraph");
	public static HashMap <String,Boolean> visited = new HashMap<String,Boolean>();//List of the methods in CallGraph
	public static void main(String[] args) {
		String appname="Miniz.apk";
									//for creation of entrypoint providing the application and android jar
	//Options.v().setPhaseOption("cg.cha","on");
	SetupApplication app = new	SetupApplication("android.jar",appname);
	try {
	   		   app.calculateSourcesSinksEntrypoints("SourceAndSinks.txt");		                    			
	} catch (Exception e) {
		e.printStackTrace();
   }
	soot.G.reset();
	//set soot options
	Options.v().set_src_prec(Options.src_prec_apk);
	Options.v().set_process_dir(Collections.singletonList(appname));
	Options.v().set_force_android_jar("android.jar");
	Options.v().set_whole_program(true);
	Options.v().set_allow_phantom_refs(true);
	//For Handling the Junit test cases output the result into Jimple files
	Options.v().set_output_format(Options.output_format_J);
	//For the code execution 
	//Options.v().set_output_format(Options.output_format_dex);
	Options.v().setPhaseOption("cg.spark verbose:true","on");
	Options.v().setPhaseOption("on-fly-cg","true");
	//Renaming dummy main method as void main(String args) for sending the cfg in VASCO
	List<Type> s = new ArrayList<Type>();
	s=Arrays.asList(new Type[]{
	soot.ArrayType.v(soot.RefType.v("java.lang.String"), 1)
	});
	//creating dummy main
	SootMethod entryPoint =app.getEntryPointCreator().createDummyMain();
	entryPoint.setName("main");
	entryPoint.setParameterTypes(s);
	Chain<Unit> u1=entryPoint.getActiveBody().getUnits();
    Unit first=u1.getFirst();
    //Replacing the constant intitialization of dummy main variable by randomnumber for VASCO doesnt remove any branches from dummy main
    Scene.v().loadClassAndSupport("java.lang.Math");
    SootMethod sm=Scene.v().getMethod("<java.lang.Math: double random()>");
    SootMethodRef smRef=sm.makeRef();
    if(first instanceof AssignStmt){
    	Value lhsOp = ((AssignStmt) first).getLeftOp();
        Value rhsOp = ((AssignStmt) first).getRightOp();
        if(lhsOp instanceof Local && rhsOp instanceof Constant){
        	u1.removeFirst();
            u1.addFirst(Jimple.v().newAssignStmt(lhsOp, Jimple.v().newStaticInvokeExpr(smRef)));
         }
     }

    Scene.v().loadNecessaryClasses();
    System.out.println("Class::--"+entryPoint.getDeclaringClass()+"\n Dummy Main....-->"+ entryPoint.getActiveBody());
	Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
	Options.v().set_main_class(entryPoint.getDeclaringClass().getName());
	PackManager.v().runPacks();
	cg = Scene.v().getCallGraph();
 	
	
	//function to get all the methods existing in the call graph
	recursive(entryPoint);
	//Printing the methods in the call graph 
	for(Entry<String, Boolean> eSet : visited.entrySet()){
			String value=eSet.getKey().toString();
			System.out.println("Methods in callg graph: "+value);
	}
	
	List<SootMethod> unusedmethods=new ArrayList<SootMethod>();	
	System.out.println("All::-->");
	//Fetching all the methods in the Application and storing the unused methods in a list(unusedmethods)
	Chain<SootClass> clas=Scene.v().getApplicationClasses();
	for(SootClass abc1:clas){
	    	SootClass sClass=Scene.v().loadClassAndSupport(abc1.getName());
	    	List <SootMethod> methods=sClass.getMethods();
			   	for(SootMethod a: methods){
	    		System.out.println( a.getSignature());
	    		if(visited.containsKey(a.getSignature()))
	    			continue;
	    		else{
	    			//Handling classes which extends View and the attributes are not defined in Layout.xml , Handling all init & access functions and Handler functions along with SQLiteDatabase Helper  
	    		    			if(!(a.getSubSignature().contains("void <init>") || a.getSubSignature().contains("void <clinit>") || a.getSubSignature().contains("access$") || a.getDeclaringClass().getSuperclass().getName().contains("SQLiteOpenHelper"))){
	    		    				if(a.getDeclaringClass().getSuperclass().getName().equals("android.view.View") || a.getDeclaringClass().getSuperclass().getName().contains("android.os.Handler")){
	    		    					List<SootMethod> methodlist=a.getDeclaringClass().getSuperclass().getMethods();
	    			    				int flag=0;
	    			    				for(SootMethod ab: methodlist){
	    			    					if(ab.getSubSignature()==a.getSubSignature()){// && !unusedmethods.contains(a))
	    			    						flag=1;
	    			    						break;
	    			    					}
	    			    				}
	    			    				if(flag!=1)
	    			    					unusedmethods.add(a);
	    			    			}
	    		    				else
	    			    				unusedmethods.add(a);
	    				}
	    				
	    			
	    		}

	    	}
	    }
	    
	    //Handling the functions when the class implements interface
	    List<SootMethod> delm = new ArrayList<SootMethod>();
	    List<SootMethod> un=unusedmethods;
	   for(SootMethod m:un){
		   	   if(m.getDeclaringClass().getInterfaceCount()>0){
					   Chain<SootClass> interfaces=m.getDeclaringClass().getInterfaces();
					   for(SootClass c:interfaces){
						   List<SootMethod> smi=c.getMethods();
						   for(SootMethod m1:smi){
							   if(m1.getName().contains(m.getName())){
								   delm.add(m);				   
							   }
						   }
						   
					   }
			   
			 }
	   }
	   for(SootMethod m1:delm){
		   unusedmethods.remove(m1);
	   }

	   //Handling Reflection methods
	   for(SootMethod a: un){
			SootClass classWeNeedToLoad = a.getDeclaringClass();
			for(Map.Entry<String,String> entry: Driver.runmethods.entrySet()){
			//	if(!(entry.getKey().contains(classWeNeedToLoad.toString()) && a.getName().contains(entry.getValue())))
				if(entry.getValue().contains(a.getName()) && entry.getKey().contains(classWeNeedToLoad.toString())){
					unusedmethods.remove(a);
				}
			}
		} 
	   
	   //Remove the unused methods	    
	    for(SootMethod a: unusedmethods){
	    	SootClass classWeNeedToLoad = a.getDeclaringClass();
	    	classWeNeedToLoad.removeMethod(a);
		}
	

	//Calling Vasco for interprocedural analysis    
	InterproceduralAnalysis ipa = new InterproceduralAnalysis(new EmptyReporter());
	ipa.doAnalysis();
	print();
	//Deleting the ifs with constant result
	deleteifs(ifStmts);
	
	//Write the apk
	PackManager.v().writeOutput();
}   
	
	public static void print(){

		for (Map.Entry<SootMethod, Map<Unit, Boolean>> entry : ifStmts.entrySet())
		{
			System.out.println("method name: "+entry.getKey().getSignature());
		Map<Unit,Boolean> val=entry.getValue();
		for (Map.Entry<Unit, Boolean> entry1 : val.entrySet()){
		System.out.println(entry1.getKey()+"CONDITION"+entry1.getValue());
		}

		}
		
	}
	public static void running(String calle,EmptyReporter r){
		String [] abc={calle};
		main(abc);
		Map<SootMethod, Map<Unit, Boolean>> abcd=ifStmts;
		for (Map.Entry<SootMethod, Map<Unit, Boolean>> entry : ifStmts.entrySet())
		{

		Map<Unit,Boolean> val=entry.getValue();
		for (Map.Entry<Unit, Boolean> entry1 : val.entrySet()){
		r.report(entry1.getKey(),entry1.getValue());
		}

		}

	}
	

//Function used to delete the if conditions which yield a constant result for each execution
	public static void deleteifs (Map<SootMethod, Map<Unit, Boolean>> ifStmts){
		Set<SootMethod> m= ifStmts.keySet();
		//boolean j_Unit_Test = false;
		for(SootMethod s1:m){
		 Chain<Unit> units =s1.getActiveBody().getUnits();
		 System.err.println(units);
		 Body b=s1.getActiveBody();
		 Map<Unit,Boolean> res=ifStmts.get(s1);
			 for (Map.Entry<Unit,Boolean> entry : res.entrySet())
			 {
				 Boolean result=entry.getValue();
				 Unit unit=entry.getKey();
				 System.out.println(entry.getKey() + "/" + entry.getValue());
				 if(result!=null)
					 if (result == true) {
						// if condition of else always true, convert if to goto
						System.out.println("[Conditional Branch Folding] else condition is true, change statement into target ");
						Stmt newStmt = Jimple.v().newGotoStmt(((IfStmt)unit).getTarget());
						System.out.println("[Conditional Branch Folding] Changing : " + unit + " into: " + newStmt);
						units.insertAfter(newStmt, unit);
						System.out.println("[Conditional Branch Folding] Removing statement : " + unit);
						units.remove(unit);
						}
						else {
						// if condition of else is false remove both statement and target
						System.out.println("[Conditional Branch Folding] else condition is false, remove both target and statement");
						units.remove(unit);
						/* check if contain else or not, if statement s does not contain "goto (branch)" --> statement contains else
						we need to remove else block
						*/
						}
					}
		//Removing the unreachable code in the procedure
		if(!j_Unit_Boolean) {
			UnreachableCodeEliminator.v().transform(b);
		}
		
	//	System.out.println("reeee");
		}
	
	}
	
//Function used to get all the methods in the callgraph	
	static void recursive(SootMethod method){
    	visited.put(method.getSignature(),true);
    	Iterator <MethodOrMethodContext> target_1=new Targets(cg.edgesOutOf(method));
 
    	if(target_1!=null){
    		while(target_1.hasNext())
    		{
    			SootMethod target_2=(SootMethod)target_1.next();
    			System.out.println("\t"+ target_2.getSignature().toString());
    			if(!visited.containsKey(target_2.getSignature()))
    				recursive(target_2);
    		}
    	}
 	}
	
}