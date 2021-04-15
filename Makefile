TOP_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PROJECT_NAME := deploymentboard

start-services:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f ${TOP_DIR}/dev-env/docker-compose-services.yml up -d --build

stop-services:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f ${TOP_DIR}/dev-env/docker-compose-services.yml -f ${TOP_DIR}/dev-env/docker-compose-deploymentboard.yml down --remove-orphans --volumes

build:
	@cd ${TOP_DIR} && \
	mvn clean install && \
	docker load -i ${TOP_DIR}/webapp/target/jib-image.tar

start-deploymentboard:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f ${TOP_DIR}/dev-env/docker-compose-services.yml -f ${TOP_DIR}/dev-env/docker-compose-deploymentboard.yml up -d

tail-logs:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f ${TOP_DIR}/dev-env/docker-compose-services.yml -f ${TOP_DIR}/dev-env/docker-compose-deploymentboard.yml logs -f

show-status:
	@cd ${TOP_DIR} && \
	TOP_DIR=${TOP_DIR} docker-compose -p ${PROJECT_NAME} -f ${TOP_DIR}/dev-env/docker-compose-services.yml -f ${TOP_DIR}/dev-env/docker-compose-deploymentboard.yml ps
