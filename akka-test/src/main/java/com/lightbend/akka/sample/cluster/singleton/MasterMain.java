package com.lightbend.akka.sample.cluster.singleton;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;

public class MasterMain {
	public static void main(String[] args) throws Exception {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2552)
				.withFallback(ConfigFactory.load("master"));

		ActorSystem system = ActorSystem.create("ClusterSystem", config);

		final ClusterSingletonManagerSettings settings = ClusterSingletonManagerSettings.create(system);
		ActorRef master = system.actorOf(
				ClusterSingletonManager.props(Props.create(Master.class),
						TestSingletonMessages.end(), settings),
				"master");
		System.out.println(master);



		
		ClusterSingletonProxySettings proxySettings = ClusterSingletonProxySettings.create(system);
		ActorRef masterProxy = system.actorOf(ClusterSingletonProxy.props("/user/master", proxySettings),
				"masterProxy");
		System.out.println(masterProxy);
		masterProxy.tell(TestSingletonMessages.ping(), ActorRef.noSender());
	}

}
