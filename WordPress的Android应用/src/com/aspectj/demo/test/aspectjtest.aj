package com.aspectj.demo.aspect;  
  
import com.commonsware.cwac.thumbnail.*;
import com.commonsware.cwac.cache.*;
import org.wordpress.android.*;
import org.wordpress.android.models.*;
import org.wordpress.android.util.*;
import org.xmlrpc.android.*;

public aspect aspectjtest {  
	pointcut getPoint() : execution(* *get*(..));
	pointcut getCfow() : cflow(getPoint());
	pointcut setPoint() : execution(* *set*(..));
	pointcut setCfow() : cflow(setPoint());
	
	before() :setPoint(){
		System.out.println("enter:" +thisJoinPoint);
		pointcut callInitialPointcut() : call(
		void MyClass.foo(int, String));
		pointcut cflowPointcut() : cflowbelow(callInitialPointcut());
		before() : cflowPointcut() && !within(CFlowBelowRecipe +)
		{
		     System.out.println(
		         "Join Point Kind: " + thisJoinPoint.getKind());
		     System.out.println(
		         "Signature: "
		            + thisJoinPoint.getStaticPart().getSignature());
		     System.out.println(
		         "Source Line: "
		            + thisJoinPoint.getStaticPart().getSourceLocation());
		}
	}
} 

