TOP_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

start-services:
	@cd ${TOP_DIR} && \
	docker-compose -f docker-compose.yml up -d --build

stop-services:
	@cd ${TOP_DIR} && \
	docker-compose -f docker-compose.yml down
