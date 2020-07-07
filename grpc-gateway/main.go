package main

import (
	"context"
	"net/http"
	"os"

	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"github.com/sirupsen/logrus"
	"google.golang.org/grpc"
	"google.golang.org/grpc/status"

	"github.com/rainist/idl/gen/go/apis/external/v1/result"
	"github.com/rainist/idl/gen/go/apis/v1/collectcard"
)

func wildcardHeaderMatcher(key string) (string, bool) {
	return key, true
}

func banksaladHTTPError(_ context.Context, _ *runtime.ServeMux, marshaler runtime.Marshaler, w http.ResponseWriter, _ *http.Request, err error) {
	w.Header().Set("Content-type", marshaler.ContentType())
	w.WriteHeader(runtime.HTTPStatusFromCode(status.Code(err)))

	resp := &result.ErrorResult{}

	if s, ok := status.FromError(err); ok {
		for _, d := range s.Details() {
			errorResult, ok := d.(*result.ErrorResult)
			if ok {
				resp = errorResult
				break
			}
		}
	}

	_ = marshaler.NewEncoder(w).Encode(resp)
}

func ServeHTTP(grpcHost string, httpPort string) error {
	runtime.HTTPError = banksaladHTTPError

	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := runtime.NewServeMux(
		runtime.WithMarshalerOption(
			runtime.MIMEWildcard,
			&runtime.JSONPb{
				OrigName:     true,
				EmitDefaults: true,
			},
		),
		runtime.WithIncomingHeaderMatcher(wildcardHeaderMatcher),
	)

	options := []grpc.DialOption{
		grpc.WithInsecure(),
	}

	if err := collectcard.RegisterCollectcardHandlerFromEndpoint(
		ctx,
		mux,
		grpcHost,
		options,
	); err != nil {
		return err
	}

	return http.ListenAndServe(":"+httpPort, mux)
}

func getEnv(key, defaultValue string) (value string) {
	value = os.Getenv(key)
	if value == "" {
		if defaultValue != "" {
			value = defaultValue
		} else {
			logrus.Fatalf("missing required environment variable: %v", key)
		}
	}
	return value
}

func main() {
	logrus.SetFormatter(&logrus.JSONFormatter{})
	log := logrus.StandardLogger()

	grpcPort := getEnv("SERVICE_GRPC_PORT", "6565")
	grpcHost := "localhost:" + grpcPort
	httpPort := getEnv("SERVICE_HTTP_PORT", "8080")

	log.WithField("grpcPort", grpcPort).WithField("httpPort", httpPort).Info("starting collectcard HTTP server")

	if err := ServeHTTP(grpcHost, httpPort); err != nil {
		log.Fatal(err)
	}
}
