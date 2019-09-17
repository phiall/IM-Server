#!/bin/sh
PID=$(cat /var/run/fishing-chat.pid)
kill -9 $PID

