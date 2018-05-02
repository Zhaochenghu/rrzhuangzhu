#!/bin/bash
SRC_DIR=./
DST_DIR=./gen

rm -R -f $DST_DIR

#android
mkdir -p $DST_DIR/java
protoc -I=$SRC_DIR --java_out=$DST_DIR/java/ $SRC_DIR/*.proto

#objctc
mkdir -p $DST_DIR/objectc
protoc -I=$SRC_DIR --objc_out=$DST_DIR/objectc/ $SRC_DIR/*.proto

#/charging/model/src/main/java/com/sojoline/model/protobufdata
APP_PKG=com/sojoline/model/protobufdata
APP_DIR=../../model/src/main/java/$APP_PKG

if ((!$?)); then
	echo 'Compile success...'
	echo 'Copy file to Android proto data file'
	rm -f $APP_DIR/*.java
	cp -f ./gen/java/$APP_PKG/* $APP_DIR/

#	echo 'Copy file to IOS protp data file'
#	rm -f ../iOS/SKDMC_IOS/SKDMC_IOS/ProtoBufData/*
#	cp -r ./gen/objectc/* ../iOS/SKDMC_IOS/SKDMC_IOS/ProtoBufData/
fi

