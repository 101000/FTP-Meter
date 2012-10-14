<h1>FTP Meter</h1>
FTP Meter is a simple Java FTP client for testing your FTP connection speed/performance.<br />
It send random in-memory byte to your FTP Server for N times or running an endless loop.<br />
Make sure to enable file overwriting on your FTP server. <br />
<br />
It makes use of:<br />
Apache Commons Daemon<br />
HSQLDB - used as log store (results informations are stored here)<br />
 <br />
<h2>Important source files</h2>
ftp.properties - Configuration file (ftp server ip/port, ftp user/password, filesize to send)<br />