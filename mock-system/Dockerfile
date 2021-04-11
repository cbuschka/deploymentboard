FROM nginx:1.15.9

ENV VERSION_RESPONSE "{}"
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV LANG en_US.utf8

EXPOSE 80
COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT [ "/entrypoint.sh" ]
STOPSIGNAL SIGTERM

CMD ["nginx", "-g", "daemon off;"]