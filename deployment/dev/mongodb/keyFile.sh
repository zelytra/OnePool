openssl rand -base64 756 > ${PWD}/rs_keyfile
chmod 600 ${PWD}/rs_keyfile
chown 9999:9999 ${PWD}/rs_keyfile