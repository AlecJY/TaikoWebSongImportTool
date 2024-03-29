# Taiko Web Song Import Tool
A Tool for Batch Import TJAs to taiko-web

***This tool only support TJA file with ogg or mp3 songs***

## How To Build
Windows:
```bat
gradlew.bat fatJar
```

Linux, macOS or other Unix-like systems:
```shell
./gradlew fatJar
```

Then the Jar file will be generated in "./build/libs" directory.

## How to Use

```shell
java -jar TaikoWebSongImportTool-xxx.jar [options] {TJAs folder} {taiko-web songs folder} {taiko-web URL} {admin username} {admin password} 
```

For example:
```shell
java -jar TaikoWebSongImportTool-xxx.jar /home/user/songs /srv/taiko-web/public/songs http://taiko.example.com admin supersecretpassword 
```

For more details:
```shell
java -jar TaikoWebSongImportTool-xxx.jar -h
```
