package com.lightbend.akka.sample.test;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;

public class StartStopActor1 extends AbstractActor {
	private ActorRef child;

	@Override
	public void preStart() {
		System.out.println("first started");
		child = getContext().actorOf(Props.create(StartStopActor2.class), "second");
		// System.out.println("Second : " + child);
	}

	@Override
	public void postStop() {
		System.out.println("first stopped");
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().matchEquals("stop", s -> {
			getContext().stop(getSelf());
		}).matchEquals("failChild", f -> {
			child.tell("fail", getSelf());
		}).build();
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		// TODO Auto-generated method stub
		return super.supervisorStrategy();
	}
}
