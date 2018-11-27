package com.lightbend.akka.sample.cluster.singleton;

import com.lightbend.akka.sample.cluster.singleton.TestSingletonMessages.Unregistration;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;

public class Worker extends AbstractActor {

	private ActorRef masterProxy = getContext().actorOf(
			ClusterSingletonProxy.props("/user/master",
					ClusterSingletonProxySettings.create(getContext().getSystem())),
			"masterProxy");

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
		return receiveBuilder().matchEquals("end", message -> {
			System.out.println("end master");
			System.out.println(masterProxy);
			masterProxy.tell(TestSingletonMessages.end(), getSelf());
		}).match(Unregistration.class, message -> {
			System.out.println("unregister master");
			getSender().tell(TestSingletonMessages.unregistrationOk(), getSelf());
		}).matchEquals("ping", message -> {
			System.out.println("ping");
			masterProxy.tell(TestSingletonMessages.ping(), getSelf());
		}).build();
	}

}
