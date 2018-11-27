package com.lightbend.akka.sample.iot;

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class IotMain {

	public static void main(String[] args) throws IOException {
		final ActorSystem system = ActorSystem.create("iot-system");

		try {
			ActorRef supervisor = system.actorOf(IotSupervisor.props(), "iot-supervisor");

			System.out.println("Press ENTER to exit the system");
			System.in.read();
		} finally {
			system.terminate();
		}
	}


}
