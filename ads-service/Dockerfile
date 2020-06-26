FROM golang:1.12-alpine as builder
RUN apk add --no-cache ca-certificates git && \
      wget -qO/go/bin/dep https://github.com/golang/dep/releases/download/v0.5.0/dep-linux-amd64 && \
      chmod +x /go/bin/dep

COPY . /go/src/ads-service/
WORKDIR /go/src/ads-service/

RUN set -x && \
    go get github.com/golang/dep/cmd/dep && \
    dep ensure -v

RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -o main .

FROM alpine as release
RUN apk add --no-cache ca-certificates 
WORKDIR /root/
COPY --from=builder /go/src/ads-service/ .
EXPOSE 8001
ENTRYPOINT ["./main"]

