package com.lightbend.akka.sample.cluster.transformation;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class BackendTest {
	public static void main(String[] args) {
		System.out.println("Start transformationBackend");
		
		
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2552).withFallback(ConfigFactory.load());
		ActorSystem actorSystem = ActorSystem.create("ClusterSystem", config);
		final ActorRef transformationBackend = actorSystem.actorOf(Props.create(TransformationBackend.class),
				"transformationBackend");

		System.out.println("Started transformationBackend "+transformationBackend);
	}
}
