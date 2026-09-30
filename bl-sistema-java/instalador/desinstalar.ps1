# Desinstalador do B&L CEO — remove app, atalhos e o registro do Windows.
$ErrorActionPreference = 'SilentlyContinue'
$Nome   = 'B&L CEO'
$Raiz   = Join-Path $env:LOCALAPPDATA 'Programs\BL CEO'
$RegKey = 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\BLCEO'

Get-Process 'B&L CEO' -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 1

$menu = Join-Path $env:APPDATA 'Microsoft\Windows\Start Menu\Programs'
Remove-Item (Join-Path $menu "$Nome.lnk") -Force -ErrorAction SilentlyContinue
Remove-Item (Join-Path ([Environment]::GetFolderPath('Desktop')) "$Nome.lnk") -Force -ErrorAction SilentlyContinue
Remove-Item $RegKey -Recurse -Force -ErrorAction SilentlyContinue

# apaga a pasta do app (menos este script, que ainda esta rodando de dentro dela)
Start-Process powershell -ArgumentList "-NoProfile -WindowStyle Hidden -Command `"Start-Sleep 2; Remove-Item '$Raiz' -Recurse -Force`""
