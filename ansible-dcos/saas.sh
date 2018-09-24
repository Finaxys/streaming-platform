#!/bin/bash
var_folder=./group_vars/
dest_file=all.yaml
src_file=all.example

src=$var_folder$src_file
dest=$var_folder$dest_file

bin='SAAS'

if [[ $# -eq 0 ]]
then
    echo -e $bin':\tNo conf file supplied.\n\tUse the default config file "'$src'".'
else
    if [ ! -f $var_folder$1 ]; then
	echo $bin':\tFile "'$$var_folder$1'" not found.\n\tAbort.' && exit 1
    fi
    src=$var_folder$1
    echo $bin': Use the specified config file "'$src'".'
fi

echo $bin': Copy '$src' to '$dest && cp $src $dest
echo $bin': Launch the automation...' && ansible-playbook plays/main.yml -i inventory
echo $bin': Remove temporary file "'$dest'".' && rm $dest

