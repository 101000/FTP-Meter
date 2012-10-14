<h1>FTP Meter</h1>
FTP Meter is a simple Java FTP client for testing your FTP connection speed/performance.
It send random in-memory byte to your FTP Server for N times or running an endless loop.
Make sure to enable file overwriting on your FTP server. 

It makes use of:
Apache Commons Daemon
HSQLDB - used as log store (results informations are stored here)
 
<h2>Important source files</h2>
ftp.properties - Configuration file (ftp server ip/port, ftp user/password, filesize to send)<br />