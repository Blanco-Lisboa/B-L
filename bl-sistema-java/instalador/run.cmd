@echo off
rem Bootstrap do instalador: extrai o payload e roda a instalacao (por usuario).
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%~dp0payload.zip' -DestinationPath '%~dp0conteudo' -Force; & '%~dp0conteudo\instalar.ps1'"
