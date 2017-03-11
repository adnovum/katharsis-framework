package io.katharsis.brave.internal;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.BraveExecutorService;
import com.github.kristofa.brave.okhttp.BraveTracingInterceptor;

import io.katharsis.client.http.okhttp.OkHttpAdapterListener;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class OkHttpBraveIntegration implements OkHttpAdapterListener {

	private Brave brave;

	public OkHttpBraveIntegration(Brave brave) {
		this.brave = brave;
	}

	@Override
	public void onBuild(OkHttpClient.Builder builder) {
		BraveTracingInterceptor interceptor = buildInterceptor();

		BraveExecutorService tracePropagatingExecutor = buildExecutor(builder);

		builder.addInterceptor(interceptor);
		builder.addNetworkInterceptor(interceptor);
		builder.dispatcher(new Dispatcher(tracePropagatingExecutor));
	}

	protected BraveExecutorService buildExecutor(okhttp3.OkHttpClient.Builder builder) {
		return new BraveExecutorService(new Dispatcher().executorService(), brave.serverSpanThreadBinder());
	}

	protected BraveTracingInterceptor buildInterceptor() {
		BraveTracingInterceptor.Builder tracingBuilder = BraveTracingInterceptor.builder(brave);
		return tracingBuilder.build();
	}
}
