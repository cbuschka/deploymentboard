FROM ubuntu:20.04

RUN export DEBIAN_FRONTEND=noninteractive; apt-get update \
	&& apt-get install --yes --no-install-recommends locales curl procps unzip zip inotify-tools vim dnsutils openssh-server supervisor net-tools apt-utils less sudo git \
	&& apt-get autoremove -y \
        && rm -rf /var/lib/apt/lists/*

ENV LANG en_US.utf8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
	&& echo $TZ > /etc/timezone \
	&& locale-gen "en_US.UTF-8" \
	&& dpkg-reconfigure --frontend=noninteractive locales

RUN useradd -u 10000 -G sudo git -m -s /bin/bash -d /home/git && \
	echo "git:asdfasdf" | chpasswd
ADD assets/ /

RUN mkdir /var/run/sshd && chmod 700 /var/run/sshd
RUN chown -R git:git /home/git && \
	chmod 600 /home/git/.ssh/* && \
	chmod 700 /home/git/.ssh/ && \
	mkdir /srv/git && chown git.git /srv/git && chmod 750 /srv/git

ADD --chown=git:git ui.tgz /srv/git/
ADD --chown=git:git backend.tgz /srv/git/

EXPOSE 22
CMD [ "/usr/bin/supervisord", "-c", "/etc/supervisord.conf" ]
