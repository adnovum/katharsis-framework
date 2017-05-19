package io.katharsis.operations;

import javax.xml.bind.annotation.XmlRootElement;

import io.katharsis.core.engine.document.Document;

@XmlRootElement
public class OperationResponse extends Document {

	private int status;


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
