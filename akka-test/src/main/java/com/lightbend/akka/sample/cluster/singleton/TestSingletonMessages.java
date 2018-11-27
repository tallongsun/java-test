package com.lightbend.akka.sample.cluster.singleton;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TestSingletonMessages {

	public static class End implements Serializable {
	}
	public static class Unregistration implements Serializable{
	}
	public static class UnregistrationOk implements Serializable {
	}

	public static class Ping implements Serializable {
	}
	public static class Pong implements Serializable {
	}



	public static UnregistrationOk unregistrationOk() {
		return new UnregistrationOk();
	}

	public static End end() {
		return new End();
	}

	public static Ping ping() {
		return new Ping();
	}

	public static Pong pong() {
		return new Pong();
	}

	public static Unregistration unregistration() {
		return new Unregistration();
	}
}
