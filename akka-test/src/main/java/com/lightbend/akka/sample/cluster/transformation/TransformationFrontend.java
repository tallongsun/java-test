package com.lightbend.akka.sample.cluster.transformation;

import java.util.ArrayList;
import java.util.List;

import com.lightbend.akka.sample.cluster.transformation.TransformationMessages.JobFailed;
import com.lightbend.akka.sample.cluster.transformation.TransformationMessages.TransformationJob;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;

public class TransformationFrontend extends AbstractActor {
	List<ActorRef> backends = new ArrayList<ActorRef>();
	int jobCounter = 0;

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(TransformationJob.class, message -> {
			if (backends.isEmpty()) {
				TransformationJob job = (TransformationJob) message;
				getSender().tell(new JobFailed("Service unavailable, try again later", job), getSender());
			} else {
				TransformationJob job = (TransformationJob) message;
				jobCounter++;
				backends.get(jobCounter % backends.size()).forward(job, getContext());
			}
		}).match(Terminated.class, message -> {
			Terminated terminated = (Terminated) message;
			backends.remove(terminated.getActor());
		}).matchEquals(TransformationMessages.BACKEND_REGISTRATION, message -> {
			getContext().watch(getSender());
			backends.add(getSender());

		}).build();
	}

}
