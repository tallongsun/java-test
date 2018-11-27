package com.lightbend.akka.sample.cluster.transformation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

public class FrontendTest {
	public static void main(String[] args) {
		System.out.println("Start transformationFrontend");

		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 0)
				.withFallback(ConfigFactory.load());
		ActorSystem actorSystem = ActorSystem.create("ClusterSystem", config);
		final ActorRef transformationFrontend = actorSystem.actorOf(Props.create(TransformationFrontend.class),
				"transformationFrontend");

		System.out.println("Started transformationFrontend");

		final FiniteDuration interval = FiniteDuration.create(2, TimeUnit.SECONDS);
		final Timeout timeout = new Timeout(FiniteDuration.create(5, TimeUnit.SECONDS));
		final ExecutionContext ec = actorSystem.dispatcher();
		final AtomicInteger counter = new AtomicInteger();
		actorSystem.scheduler().schedule(interval, interval, new Runnable() {
			public void run() {
				Future<Object> future = Patterns.ask(transformationFrontend,
						new TransformationMessages.TransformationJob("hello-" + counter.incrementAndGet()), timeout);

				future.onSuccess(new OnSuccess<Object>() {
					public void onSuccess(Object result) {
						System.out.println(result.toString());
					}
				}, ec);
			}
		}, ec);
	}
}
