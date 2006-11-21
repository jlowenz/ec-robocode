Blitz Installer
---------------

Generated Scripts
-----------------

The Blitz installer creates the following scripts. These will have a .sh
extension on UNIX platforms and a .bat extension on Windows platforms 

The quickest way to get started is to simply use the "blitz" script. 
The other scripts are provided to allow for more flexibility.

1.  blitz
    Use this script for Blitz 'out ot the box'

    This scripts starts 
    
    1.1 An HTTPD on port 8080
    1.2 Reggie the Jini Lookup Service
    1.3 Mahalo the Jini TransactionManager
    1.4 An HTTPD on the port specified when you installed the software
    1.5 Blitz JavaSpace

2.  start-trans-blitz_with_httpd
    Use this script to start Blitz in transient mode

    This script starts
    
    2.1 An HTTPD on the port specified when you installed the software
    2.2 Blitz JavaSpace

3.  start-ws-<port>
    Use this script to start an HTTPD on the port specified when you 
    installed the software

4.  start-trans-blitz
    Before running this script you must run #3 script above
    Use this script to start a non-activatable version of Blitz

5.  start-jini-ws-8080
    Use this script to start an HTTP on port 8080

6.  start-reggie
    Before running this script you must run #5 script above
    Use this script to start the Jini Lookup Service

7.  start-phoenix
    Before running this script you must start the jini httpd #5 above
    Use this script to start Jini's activation system

8.  start-act-blitz
    Before running this script you must start the activation system #7 above.

9.  dashboard
    Runs the Blitz dashboard GUI
    By default this script looks for "Blitz_JavaSpace"

For support please contact blitz@dancres.org
    
