#!/bin/bash
set -e

echo "Starting MongoDB initialization script..."
openssl rand -base64 756 > ~/rs_keyfile

mongosh <<EOF
use ${MONGO_INITDB_DATABASE}

if (rs.status().ok == 0) {
  print("Initializing replica set...")
  rs.initiate(
    {
      _id: "rs0",
      version: 1,
      members: [
        { _id: 0, host: "onepool-mongo-app:27017" }
      ]
    }
  )
  print("Replica set initialized.")
} else {
  print("Replica set already initialized.")
}

if (db.getUser('${MONGO_INITDB_USER}') == null) {
  print("Creating user '${MONGO_INITDB_USER}'...")
  db.createUser({
    user: '${MONGO_INITDB_USER}',
    pwd: '${MONGO_INITDB_PWD}',
    roles: [{
      role: 'readWrite',
      db: '${MONGO_INITDB_DATABASE}'
    }]
  })
  print("User '${MONGO_INITDB_USER}' created.")
} else {
  print("User '${MONGO_INITDB_USER}' already exists.")
}
EOF

echo "MongoDB initialization script completed."
