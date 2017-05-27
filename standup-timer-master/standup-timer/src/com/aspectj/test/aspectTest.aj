package com.aspectj.test;
//import com.aspectj.demo.test.pointcut;
//import com.ccxt.whl.*;
//import com.ccxt.whl.activity.*;
//import com.ccxt.whl.adapter.*;
//import com.ccxt.whl.db.*;
//import com.ccxt.whl.domain.*;
//import com.ccxt.whl.gushi.*;
//import com.ccxt.whl.service.*;
//import com.ccxt.whl.task.*;
//import com.ccxt.whl.utils.*;
//import com.ccxt.whl.video.util.*;
//import com.ccxt.whl.widget.photoview.*;

public aspect aspectTest{
	pointcut getPoint() : execution(* *get*(..));
	pointcut getCfow() : cflow(getPoint());
	pointcut setPoint() : execution(* *set*(..));
	pointcut setCfow() : cflow(setPoint());
	pointcut createPoint() : execution(* *create*(..));
	before() :setPoint(){
		System.out.println("enter:" +thisJoinPoint);}
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
	//}   


	   //}
}
