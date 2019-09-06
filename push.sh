#!/usr/bin/env bash

set -eou pipefail

version=$(cat ./resources/base.edn | grep  ':version .*"[0-9\.]*"' | grep -oEi '[0-9]+\.[0-9]+\.[0-9]+')
docker build -t pitfall:${version} .
docker tag pitfall:${version} formicarium/pitfall:${version}
docker tag pitfall:${version} formicarium/pitfall:latest
docker push formicarium/pitfall:${version}
docker push formicarium/pitfall:latest
