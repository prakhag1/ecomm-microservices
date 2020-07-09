package main

import (
  "encoding/json"
  "fmt"
  "net/http"
  "log"
  "os"
  "io/ioutil"

  "contrib.go.opencensus.io/exporter/stackdriver"
  "go.opencensus.io/trace"
  "go.opencensus.io/plugin/ochttp"
  "go.opencensus.io/stats/view"
  "go.opencensus.io/plugin/ochttp/propagation/b3"
)

type ProductCategory struct {
    Category string
}

func serveAd(w http.ResponseWriter, r *http.Request) {
  var p ProductCategory
  var results []map[string]interface{}

  // Nested trace span
  ctx := r.Context()
  HTTPFormat := &b3.HTTPFormat{}
    sc, ok := HTTPFormat.SpanContextFromRequest(r)
    if ok {
      _, span := trace.StartSpanWithRemoteParent(ctx, "Ads Backend", sc)
      defer span.End()
    }
    
  // Fetch ad
  decoder := json.NewDecoder(r.Body)
  err := decoder.Decode(&p)
  if err != nil {
    panic(err)
  }

  jsonFile, err := os.Open("ads.json")
  if err != nil {
    fmt.Println(err)
  }
  defer jsonFile.Close()

  byteValue, _ := ioutil.ReadAll(jsonFile)
  json.Unmarshal([]byte(byteValue), &results)
  for key, result := range results {
    _ = key
    ads := result["ads"].(map[string]interface{})
    if result["name"] == p.Category {
      // Ideally the response should be in json, but resorting 
      // to string for the purposes of demonstrating functionality
      str := fmt.Sprintf("%v", ads["redirecturl"])+":"+fmt.Sprintf("%v", ads["text"])
      w.Write([]byte(str))
    }
  }
}

func main() {
  // Create and register a OpenCensus Stackdriver Trace exporter.
  exporter, err := stackdriver.NewExporter(stackdriver.Options{
    ProjectID: os.Getenv("GCP_PROJECT_ID"),
  })
  
  if err != nil {
    log.Fatal(err)
  }
  trace.RegisterExporter(exporter)
  trace.ApplyConfig(trace.Config{DefaultSampler: trace.AlwaysSample()})
    
  // Handle incoming request
  mux := http.NewServeMux()
  mux.HandleFunc("/servead", serveAd)
  h := &ochttp.Handler{Handler: mux}
  if err := view.Register(ochttp.DefaultServerViews...); err != nil {
    log.Fatal("failed to register default views")
  }
  
  log.Println("Starting server on :8001...")
  log.Fatal(http.ListenAndServe(":8001", h))
}
