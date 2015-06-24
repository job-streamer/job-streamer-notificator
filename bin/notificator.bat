@echo off

pushd %0\..\..

set /p VERSION=<VERSION

java -cp dist\job-streamer-notificator-%VERSION%.jar;"lib\*" clojure.main -m job-streamer.notificator.core

pause

