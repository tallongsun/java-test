package com.lightbend.akka.sample.cluster.singleton;

import com.lightbend.akka.sample.cluster.singleton.TestSingletonMessages.End;
import com.lightbend.akka.sample.cluster.singleton.TestSingletonMessages.Ping;
import com.lightbend.akka.sample.cluster.singleton.TestSingletonMessages.UnregistrationOk;

import akka.actor.AbstractActor;

public class Master extends AbstractActor {
	public Master() {

	}

	@Override
	public void preStart() {
		System.out.println("prestart");
	}
	
	@Override
	public void postStop() throws Exception {
		System.out.println("poststop");
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(End.class, message -> {
			System.out.println("tell worker unregister master");
			getSender().tell(TestSingletonMessages.unregistration(), getSelf());
		}).match(UnregistrationOk.class, message -> {
			System.out.println("stop");
			getContext().stop(getSelf());
		}).match(Ping.class, message -> {
			System.out.println("pong");
			getSender().tell(TestSingletonMessages.pong(), getSelf());
		}).build();
	}

}
