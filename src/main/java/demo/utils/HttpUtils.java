package demo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;

import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.TextFormat;

public class HttpUtils {
  private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();

  @SuppressWarnings("rawtypes")
  private static final TextFormat.Setter setter =
      new TextFormat.Setter<HttpURLConnection>() {
        public void put(HttpURLConnection carrier, String key, String value) {
          carrier.setRequestProperty(key, value);
        }
      };

  @SuppressWarnings("unchecked")
  public static String callEndpoint(String url, HttpMethod method, Tracer tracer, JSONObject req)
      throws Exception {
    StringBuilder result = new StringBuilder();
    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	
    textFormat.inject(tracer.getCurrentSpan().getContext(), conn, setter);

    conn.setDoOutput(true);
    conn.setDoInput(true);
    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    conn.setRequestMethod(method.name());
    OutputStream os = conn.getOutputStream();
    os.write(req.toString().getBytes("UTF-8"));
    os.close();

    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    rd.close();

    return result.toString();
  }
}
