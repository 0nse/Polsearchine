#Polsearchine
This is the source code of Polsearchine – a policy-based regulating search engine. Due to licencing reasons, a fundamental library is missing. It has been built using NetBeans 7.3. Polsearchine is a Java EE 6 web application which was developed to run on a [GlassFish Server Open Source Edition 3.1.2.2](https://glassfish.java.net/download-archive.html).

##Features
* Text-based search for text and image search
* Allows search for files with special `filetype:` operator
* Regulation of results possible through the Information Flow Ontology (InFO) developed by Scherp and Kasten
* Result regulations are marked explicitly as such and legitimating information will be displayed
* Graphical backend to easily add and remove regulation policies
* Policies are processed only once per Web server start
* Processed policies are stored in a database containing only indispensable information
* Includes a user and role system allowing users to login with a password
* All access is encrypted using TLS 1.0
* Interface built with Bootstrap v.3.0.0
* Replacing the backend search engine is easy

##Documentation
In general, the code is well-documented. The associated JavaDoc can be generated if needed. Additionally, extensive documentation is given through [the corresponding bachelor thesis][http://kola.opus.hbz-nrw.de/volltexte/2013/934/].
