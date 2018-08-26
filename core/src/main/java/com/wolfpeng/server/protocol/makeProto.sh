#!/bin/sh

#  makeProto.sh
#  ProtocolBuffer
#
#  Created by peng hao on 2017/11/8.
#  Copyright © 2017年 wolfpeng. All rights reserved.

protoc --java_out=. Base.proto
protoc --java_out=. Notify.proto
protoc --java_out=. Request.proto
protoc --java_out=. Response.proto
protoc --java_out=. Message.proto