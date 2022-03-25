## Location Tracking

Copyright (c) 2020-2022 Jeff Avery. May be modified and redistributed for any purpose. 

This is an Android location-tracking application meant to be used in BIOL 150. It consists of an Android client, a background service that will run in a phone and periodocally report location information, and a server which will collect and log this data.

#### Android Client
The client is contained in two projects:

* Location: this is an Android project containing a client service that needs to be installed on every phone that you wish to track. It will report a unique hardware id and location information at a specified interval to the remote service.
* WSTest: this is a test framework that can make post requests to the server, useful for testing.

#### Server

The server is a simplistic PHP script (`test.php`), whichs is hosted in a publically accessible directory and made executable. With PHP plugins installed, the script can be called by anyone posting data to its URI: location and device information are merely passed as variables. This assumes that PHP is installed, but has the advantage of not requiring a dedicated web service (and it doesn't require admin rights!)
