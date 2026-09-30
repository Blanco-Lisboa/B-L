# Instalador do B&L CEO — por usuario, sem admin, sem WiX.
# Roda a partir da pasta extraida (ao lado da pasta "app").
$ErrorActionPreference = 'SilentlyContinue'

$Nome     = 'B&L CEO'
$AppId    = 'BLCEO'
$Versao   = '0.1.0'
$Raiz     = Join-Path $env:LOCALAPPDATA 'Programs\BL CEO'
$Exe      = Join-Path $Raiz 'B&L CEO.exe'
$Origem   = Join-Path $PSScriptRoot 'app\B&L CEO'
$RegKey   = 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Uninstall\BLCEO'

Write-Host "Instalando $Nome $Versao..."

# 1) Fecha o app se estiver aberto
Get-Process 'B&L CEO' -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# 2) Remove versoes antigas (pasta solta no Downloads e instalacao anterior)
$velhos = @(
  (Join-Path $env:USERPROFILE 'Downloads\B&L CEO'),
  (Join-Path $env:USERPROFILE 'Downloads\B&L CEO.zip')
)
foreach ($v in $velhos) { if (Test-Path $v) { Remove-Item $v -Recurse -Force -ErrorAction SilentlyContinue } }
if (Test-Path $Raiz) { Remove-Item $Raiz -Recurse -Force -ErrorAction SilentlyContinue }

# 3) Copia o app novo para Programas do perfil
New-Item -ItemType Directory -Force $Raiz | Out-Null
Copy-Item (Join-Path $Origem '*') $Raiz -Recurse -Force

# 4) Guarda o desinstalador ao lado do app
Copy-Item (Join-Path $PSScriptRoot 'desinstalar.ps1') (Join-Path $Raiz 'desinstalar.ps1') -Force

# 5) Atalhos (Menu Iniciar + Area de trabalho)
$ws = New-Object -ComObject WScript.Shell
$menu = Join-Path $env:APPDATA 'Microsoft\Windows\Start Menu\Programs'
foreach ($destino in @((Join-Path $menu "$Nome.lnk"), (Join-Path ([Environment]::GetFolderPath('Desktop')) "$Nome.lnk"))) {
  $lnk = $ws.CreateShortcut($destino)
  $lnk.TargetPath = $Exe
  $lnk.WorkingDirectory = $Raiz
  $lnk.IconLocation = $Exe
  $lnk.Save()
}

# 6) Registra no Windows -> aparece em "Aplicativos instalados", com desinstalador
New-Item -Path $RegKey -Force | Out-Null
$comando = "powershell -NoProfile -ExecutionPolicy Bypass -File `"$Raiz\desinstalar.ps1`""
Set-ItemProperty $RegKey 'DisplayName'     "$Nome"
Set-ItemProperty $RegKey 'DisplayVersion'  "$Versao"
Set-ItemProperty $RegKey 'Publisher'       'Blanco & Lisboa'
Set-ItemProperty $RegKey 'DisplayIcon'     "$Exe"
Set-ItemProperty $RegKey 'InstallLocation' "$Raiz"
Set-ItemProperty $RegKey 'UninstallString' "$comando"
Set-ItemProperty $RegKey 'NoModify' 1 -Type DWord
Set-ItemProperty $RegKey 'NoRepair' 1 -Type DWord
Set-ItemProperty $RegKey 'EstimatedSize' 224000 -Type DWord

Write-Host "Instalado em $Raiz"
Start-Process $Exe
