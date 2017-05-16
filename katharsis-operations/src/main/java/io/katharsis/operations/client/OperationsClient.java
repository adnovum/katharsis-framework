package io.katharsis.operations.client;


import io.katharsis.client.KatharsisClient;

public class OperationsClient {

	private KatharsisClient katharsis;

	public OperationsClient(KatharsisClient katharsis) {
		this.katharsis = katharsis;
	}

	public OperationsCall createCall() {
		return new OperationsCall(this);
	}

	protected KatharsisClient getKatharsis() {
		return katharsis;
	}
}
