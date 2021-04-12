TOP_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

start-services:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -f docker-compose.yml up -d --build

stop-services:
	@cd ${TOP_DIR} && \
	docker-compose -f docker-compose.yml -f docker-compose-poboard.yml down --remove-orphans --volumes

build:
	@cd ${TOP_DIR} && \
	mvn clean install && \
	docker load -i ${TOP_DIR}/webapp/target/jib-image.tar
