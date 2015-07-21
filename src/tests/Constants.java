/**
 * 
 */
package tests;

/**
 * @author vaishnavi
 *
 */
public class Constants {
	
	static final String condition = "CONDITION";
	static final String conditions[] =  {
		"if $i0 != 5 goto $r1 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"again not call\", 0)CONDITIONfalse",
		"if $i1 != 0 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"Object not print\", 0)CONDITIONfalse",
		"if $i0 != 5 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"not and print\", 0)CONDITIONfalse",
		"if $i0 == 5 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or print2\", 0)CONDITIONtrue",
		"if $i0 == 6 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or print\", 0)CONDITIONfalse",
		"if $i1 == 10 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"not and not print\", 0)CONDITIONtrue",
		"if $i1 != 10 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print\", 0)CONDITIONfalse",
		"if $i0 == 5 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or print1\", 0)CONDITIONtrue",
		"if $i1 != 10 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or not print3\", 0)CONDITIONfalse",
		"if $i0 != 5 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"double and int not print\", 0)CONDITIONfalse",
		"if $i0 != 5 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print\", 0)CONDITIONfalse",
		"if $i0 != 4 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print2\", 0)CONDITIONtrue",
		"if $i1 != 10 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print2\", 0)CONDITIONfalse",
		"if $i1 != 10 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or not print2\", 0)CONDITIONfalse",
		"if $i1 != 7 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print1\", 0)CONDITIONtrue",
		"if $i0 != $i1 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"for not print\", 0)CONDITIONfalse",
		"if $i1 != 9 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print3\", 0)CONDITIONtrue",
		"if $i1 != 7 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or not print1\", 0)CONDITIONtrue",
		"if $i1 != 7 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or not print\", 0)CONDITIONtrue",
		"if $i0 != 2 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"interprocedure not print\", 0)CONDITIONfalse",
		"if $i0 == 675 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"or print3\", 0)CONDITIONfalse",
		"if $i0 != 3 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print3\", 0)CONDITIONtrue",
		"if $i0 != 5 goto $r6 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"and not print1\", 0)CONDITIONfalse",
		"if $i0 != 3 goto $r1 = staticinvoke <android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>($r0, \"inter 1 not print\", 0)CONDITIONfalse"
	};
			
}
