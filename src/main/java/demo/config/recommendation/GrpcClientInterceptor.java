package demo.config.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.propagation.TextFormat.Setter;

/** A interceptor to handle client header. */
@Component
public class GrpcClientInterceptor implements ClientInterceptor {
  @Autowired private Tracer tracer;
  private static final TextFormat B3_FORMAT = Tracing.getPropagationComponent().getB3Format();
  private static final Setter<Metadata> METADATA_SETTER = new B3Setter();

  private static class B3Setter extends Setter<Metadata> {
    @Override
    public void put(Metadata carrier, String key, String value) {
      carrier.put(Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);
    }
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    return new SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        B3_FORMAT.inject(tracer.getCurrentSpan().getContext(), headers, METADATA_SETTER);
        super.start(
            new SimpleForwardingClientCallListener<RespT>(responseListener) {
              @Override
              public void onHeaders(Metadata headers) {
                super.onHeaders(headers);
              }
            },
            headers);
      }
    };
  }
}
