# vi: ft=make

.PHONY: port-forward-prod
port-forward-prod:
	kubectl port-forward -n production deployment/cipher 9999:18081 &
	kubectl port-forward -n production deployment/connect 9998:18081 &

.PHONY: port-forward-dev
port-forward-dev:
	kubectl port-forward -n development deployment/cipher 9999:18081 &
	kubectl port-forward -n development deployment/connect 9998:18081 &

.PHONY: kill-port-forward
kill-port-forward:
	./kill-port-forward.sh
