FROM nginx:1.18

ENV VERSION_RESPONSE "{}"
ENV TZ=Europe/Berlin
ENV LANG en_US.utf8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN export DEBIAN_FRONTEND=noninteractive; apt-get update \
	&& apt-get install --yes --no-install-recommends locales curl procps unzip zip inotify-tools vim dnsutils openssh-server supervisor net-tools apt-utils less sudo \
	&& apt-get autoremove -y \
        && rm -rf /var/lib/apt/lists/*

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
	&& echo $TZ > /etc/timezone \
	&& locale-gen "en_US.UTF-8" \
	&& dpkg-reconfigure --frontend=noninteractive locales

RUN useradd -u 10000 app -m -s /bin/bash -d /home/app && \
	echo "app:asdfasdf" | chpasswd
ADD assets/ /

RUN mkdir /var/run/sshd && chmod 700 /var/run/sshd
RUN chown -R app:app /home/app && \
	chmod 600 /home/app/.ssh/* && \
	chmod 700 /home/app/.ssh/

EXPOSE 80
COPY entrypoint.sh /entrypoint.sh
USER root

ENTRYPOINT [ "/entrypoint.sh" ]

CMD [ "/usr/bin/supervisord", "-c", "/etc/supervisord.conf" ]
