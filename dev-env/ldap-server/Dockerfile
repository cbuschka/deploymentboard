FROM debian:buster-slim

RUN apt-get update \
	&& apt-get dist-upgrade -y \
    && DEBIAN_FRONTEND=noninteractive apt-get \
        install -y --no-install-recommends \
            slapd \
            ldap-utils \
            openssl \
            ca-certificates \
            tini \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir /etc/ldap/ssl /bootstrap

ADD ./assets /assets

RUN /bin/bash /assets/slapd-init.sh

VOLUME ["/etc/ldap/slapd.d", "/etc/ldap/ssl", "/var/lib/ldap", "/run/slapd"]

EXPOSE 389 636

#USER openldap

ENTRYPOINT ["/usr/bin/tini", "--", "/usr/sbin/slapd"]
CMD ["-h", "ldapi:/// ldap://0.0.0.0:389 ldaps://0.0.0.0:636", "-d", "256"]

HEALTHCHECK CMD ldapsearch -H ldap://127.0.0.1:389 -D cn=admin,dc=example,dc=com -w asdfasdf -b cn=admin,dc=example,dc=com