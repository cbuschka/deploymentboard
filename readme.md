# Deployment Board

[![Build](https://github.com/cbuschka/deploymentboard/workflows/build/badge.svg)](https://github.com/cbuschka/deploymentboard) [![Docker Image](https://img.shields.io/badge/docker.io-cbuschka%2Fdeploymentboard-yellowgreen)](https://hub.docker.com/repository/registry-1.docker.io/cbuschka/deploymentboard/tags?page=1&ordering=last_updated) [![License](https://img.shields.io/github/license/cbuschka/deploymentboard.svg)](https://github.com/cbuschka/deploymentboard/blob/main/license.txt)

### Deployment dashboard to display which story is deployed where

![Screenshot](./doc/screenshot.png)

## Prerequisites

- docker/ docker-compose
- java 11
- maven
- GNU make

## Features

- configurable environments (default: dev, int, stage, prod)
- supported auth types: rsa key, plain text password
- supported deployment info retrieval methods: http, https, scp/sftp, ssh, local shell command
- supported deployment info formats: properties, json, yaml, xml
- supported code repos: git via ssh

## Usage

```bash
docker run --name deploymentboard --rm \
  -e JAVA_TOOL_OPTIONS='-Ddeploymentboard.config=file:/config/config.yaml -Ddeploymentboard.masterpassword=' \
  -v ${PWD}/backend/src/test/resources/test-config.yaml:/config/config.yaml \
  -v ${PWD}/workspace/:/tmp/workspace:rw \
  -p 8080:8080 \
  cbuschka/deploymentboard:latest
```

## Local Development

### Start Test Environment

```bash
make start-services
```

### Develop Frontend

```bash
cd frontend/src/main/frontend && \
  nvm use && \
  yarn install && \
  yarn run start
```

### Run and Debug

Then run and debug the spring boot webapp via your preferred IDE.

### Build Docker Image

```bash
make build
```

### Test Docker Image

```bash
make start-deploymentboard
```

### Show Status of Test Environment

```bash
make show-status
```

### Tail Test Environment Logs

```bash
make tail-logs
```

### Shutdown Test Environment

```bash
make stop-services
```

## License

Copyright (c) 2021 by [Cornelius Buschka](https://github.com/cbuschka).

[Apache License, Version 2.0](./license.txt)
