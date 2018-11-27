package com.lightbend.akka.sample.cluster.singleton;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class WorkerMain {
	public static void main(String[] args) throws Exception {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 10004)
				.withFallback(ConfigFactory.load("worker"));

		ActorSystem system = ActorSystem.create("ClusterSystem", config);

		ActorRef worker = system.actorOf(Props.create(Worker.class), "worker");
		System.out.println(worker);
		
		worker.tell("ping", ActorRef.noSender());
		
		Thread.sleep(10000);
		
		worker.tell("end", ActorRef.noSender());
	}
}
