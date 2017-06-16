#!/bin/bash

#sed -i 's//new/g' client/src/app/about/about-component.js

versions=(0 0)
idx=0
while read -r line
do
  versions[$idx]=$line
  let idx++
done < "./version.txt"

current=${versions[0]}
next=${versions[1]}

echo Current version: $current
echo Bumping up to: $next

command="s/$current/$next/g"
sed -i '~' -e $command client/src/app/about/about-component.js

command="s/<version>$current/<version>$next/1"
sed -i '~' -e $command server/pom.xml

echo done!
