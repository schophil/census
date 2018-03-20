#!/bin/bash

my_home=`echo $HOME`/.census/
echo "Starting census on $my_home"

versions=(0 0)
idx=0
while read -r line
do
  versions[$idx]=$line
  let idx++
done < "./version.txt"

current=${versions[0]}
next=${versions[1]}

jarName="./server/target/census-$next.jar"
java -jar $jarName --spring.profiles.active=local --census.data=file://$my_home --census.config=config.xml
