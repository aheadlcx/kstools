cd /Users/zhangjinwei/github/kstools
aapt_path=/Users/zhangjinwei/Library/Android/sdk/build-tools/27.0.3/aapt
java -Xmx2048m -XX:-UseParallelGC -XX:MinHeapFreeRatio=15 -jar kstools.jar ++hook ./ ./src.apk ${aapt_path} 1338303158
adb install -r signed.apk