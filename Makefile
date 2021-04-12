TOP_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PROJECT_NAME := poboard

start-services:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f docker-compose-services.yml up -d --build

stop-services:
	@cd ${TOP_DIR} && \
	docker-compose -p ${PROJECT_NAME} -f docker-compose-services.yml -f docker-compose-poboard.yml down --remove-orphans --volumes

build:
	@cd ${TOP_DIR} && \
	mvn clean install && \
	docker load -i ${TOP_DIR}/webapp/target/jib-image.tar

start-poboard:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f docker-compose-services.yml -f docker-compose-poboard.yml up -d

tail-logs:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f docker-compose-services.yml -f docker-compose-poboard.yml logs -f
