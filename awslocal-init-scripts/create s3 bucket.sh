#!usrbinenv bash
set -x
awslocal s3 mb s3://staging-storage
awslocal s3 mb s3://permanent-storage
set +x