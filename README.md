# FTP-Client
FTP client implementation in Java that enables parallel upload of up to five files. 
The client keeps track of current stats during upload for each file such as:
* Percentage of uploaded file
* Elapsed time of upload
* Transfer rate

At the end of upload it shows the average transfer rate for each file and elapsed time as well as cumulative stats:
* Total upload time of all files
* Average transfer rate for all files

The client can be run without -u, -p, -server parameters. They will take the default values.
* -u : user
* -p : pass
* -server: 127.0.0.1

The -files parameter is mandatory.


## Example

**_NOTE_: For the stats to show correctly and orderly in a single line set the correct Command Prompt layout properties. 
Deselect "Wrap text output on resize" and set the width of the screen buffer to accomodate the stat showing.**

Running an example with all parameters
```
java -jar FTPClient.jar -u username -p password -server 127.0.0.1 -files data1.img;data2.img
```

Running an example with some parameters
```
java -jar FTPClient.jar -server 192.168.10.1 -files data1.img;data2.img;music.mp3;mov.mp4
```
![alt text](https://github.com/avionipevaju/FTP-Client/blob/master/examples/example.png?raw=true "Logo Title Text 1")

