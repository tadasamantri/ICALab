/**
 * 
 */
package tests;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import soot.Unit;
import soot.Singletons.Global;

import com.icalab.deadcode.*;

//import de.ecspride.sse.ica.ex1.reporting.IReporter;
//import de.ecspride.sse.ica.ex1.vasco.MainClass;

public class MinizTest extends EmptyReporter{
	
	static final int valueCheck = 1;
	static int deadForBranches = 0;
	static int deadElseBranches = 0;
	Map condtionMap = new HashMap();
	List<String> conditionListWithBranch = new LinkedList<String>();
	List conditionList = new LinkedList();
	String s="Miniz.apk";
	
	@Test
	public void targetClass1Test() {
		
		Driver.j_Unit_Boolean = true;
		Driver.running(s,new EmptyReporter(){
			@Override
			public void report(Unit ifStmt,boolean branch) {
				deadForBranches++;
				String conditions[] = Constants.conditions;
				conditionListWithBranch = Arrays.asList(conditions);
				for (int i=0; i<conditionListWithBranch.size(); i++) {
					String conditionWithBranch = (String)conditionListWithBranch.get(i);
					String branchBreaker[] = conditionWithBranch.split(Constants.condition);
					condtionMap.put(branchBreaker[0].trim(), branchBreaker[1].trim());
					conditionList = Arrays.asList(condtionMap.keySet().toArray());
					
				}
				
				int br = 0;
				if(conditionList.contains(ifStmt.toString())){
					br=1;
				}
				Assert.assertEquals(valueCheck,br);
			}
			
		});

		Assert.assertEquals(24, deadForBranches);
	}
}
