package com.epam.storage.compression.writelistenerimpl;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class WriteListenerImpl implements WriteListener {

	private ServletOutputStream output = null;
	private Queue queue = null;
	private AsyncContext context = null;
	 
	public WriteListenerImpl(ServletOutputStream sos, Queue q, AsyncContext c) {
		
		this.output = sos;
		this.queue = q;
		this.context = c;	
		
	}

	@Override
	public void onWritePossible() throws IOException {

		while (queue.peek() != null && output.isReady()) {
			String data = (String) queue.poll();
			output.print(data);
		}
		
		if (queue.peek() == null) {
			context.complete();
		}

	}

	@Override
	public void onError(Throwable e) {
		context.complete();
		e.printStackTrace();
	}

	
	
}
