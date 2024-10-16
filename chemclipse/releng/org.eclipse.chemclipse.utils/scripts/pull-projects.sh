#!/bin/bash

#*******************************************************************************
# Copyright (c) 2014, 2018 Lablicate GmbH.
# 
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
#	Dr. Philip Wenig
#       Dr. Janos Binder
#*******************************************************************************/

PULL_SCRIPT_GIT_FLAGS=" --quiet"

function pull_project {

  #
  # Apply on a valid Git repository only.
  #
  if [ -e "$1/.git" ]; then
    echo -e "git pull$PULL_SCRIPT_GIT_FLAGS project: \033[1m$1\033[0m"
    cd $1
    git pull$PULL_SCRIPT_GIT_FLAGS
    cd $pull_script_active
  fi
}

while getopts "nqvh" opt; do
  case $opt in
    n)
      PULL_SCRIPT_GIT_FLAGS=""
      ;;
    q)
      PULL_SCRIPT_GIT_FLAGS=" --quiet"
      ;;
    v)
      PULL_SCRIPT_GIT_FLAGS=" --verbose"
      ;;
    h)
      echo "Pulls all project updates" >&2
      echo "Flags: -n -- calls git pull without any argument" >&2
      echo "       -q -- calls git pull in '--quiet' mode (default)" >&2
      echo "       -v -- calls git pull in '--verbose' mode" >&2
      echo "       -h -- shows this help" >&2
      exit 1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 0
      ;;
  esac
done

echo "Start git project pull"
  pull_script_active=$(pwd)
  # ../../../ go to workspace area.
  export -f pull_project
  export PULL_SCRIPT_GIT_FLAGS
  export pull_script_active
  val=$(command -v parallel)
  if [ -z "$val" ]; then
    echo "INFO: Please consider installing 'parallel' to make this script superfast."
    for git_project in $(find ../../.. -maxdepth 1 -type d); do
      pull_project $git_project
    done
  else
  # one can play with parallel --bar or --progress but it looks ugly
    find ../../../../../ -maxdepth 1 -type d | parallel -j4 pull_project :::: -
  fi
echo "finished"
