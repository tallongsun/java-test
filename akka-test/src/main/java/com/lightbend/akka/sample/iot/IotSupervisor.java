package com.lightbend.akka.sample.iot;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class IotSupervisor extends AbstractActor {
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public static Props props() {
		return Props.create(IotSupervisor.class);
	}

	@Override
	public void preStart() {
		log.info("IoT Application started");
	}

	@Override
	public void postStop() {
		log.info("IoT Application stopped");
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
	            .build();
	}

}
