FROM golang:1.14.2 as golang

WORKDIR /collectcard/grpc-gateway
COPY . .

ARG GH_ACCESS_TOKEN
RUN git config --global url."https://${GH_ACCESS_TOKEN}@github.com/".insteadOf "https://github.com/"
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -installsuffix cgo -ldflags="-w -s" -o /go/bin/collectcard-grpc-gateway

WORKDIR /collectcard

FROM debian:stable-20190910-slim

RUN apt-get update \
    && apt-get install -y ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY --from=golang /go/bin /app
ENTRYPOINT ["app/collectcard-grpc-gateway"]
