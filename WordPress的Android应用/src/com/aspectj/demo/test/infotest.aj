package com.aspectj.demo.aspect;

public aspect infotest {
	pointcut setPoint() : execution(* *set*(..));
	before() :setPoint(){
		System.out.println("enter:" +thisJoinPoint);
	}
}
