#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

if hash VBoxManage 2>/dev/null; then
  # Bash on Mac/Linux should have VBoxManage in the PATH
  echo 'VBoxManage'
elif [[ -f '/c/Program Files/Oracle/VirtualBox/VBoxManage.exe' ]]; then
  # GitBash on Windows - default VirtualBox install location
  echo '/c/Program Files/Oracle/VirtualBox/VBoxManage.exe'
elif [[ -f '/mnt/c/Program Files/Oracle/VirtualBox/VBoxManage.exe' ]]; then
  # Linux Subsystem for Windows - default VirtualBox install location
  echo '/mnt/c/Program Files/Oracle/VirtualBox/VBoxManage.exe'
else
  echo >&2 'ERROR: VBoxManage not found'
  exit 1
fi
