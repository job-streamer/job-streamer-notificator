@echo off

if "%~1"=="" (
  goto argument_count_error
) else if not "%~2"=="" (
  goto argument_count_error
) else (
  goto run_main
)


:argument_count_error
echo "arg must be one" 1>&2
echo "usage:bin/notificator arg(edn file path)" 1>&2
exit /b 1



:run_main

pushd %0\..\..

set /p VERSION=<VERSION

java -cp dist\job-streamer-notificator-%VERSION%.jar;"lib\*" clojure.main -m job-streamer.notificator.core %~1

pause

