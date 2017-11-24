# DocAudit


### install virtualbox, https://www.virtualbox.org/wiki/Downloads
### install vagrant, https://www.vagrantup.com/downloads.html

### open a terminal
<code><pre>git clone https://github.com/rwhitak3/DocAudit
cd DocAudit
vagrant up
vagrant ssh
cd /vagrant
gradle build test

Then Try:
$ java -jar build/libs/DocAudit-1.0.jar --learn --img --file src/test/resources/fw4.pdf --savefile /tmp/docaudit/dc.kryo
INFO [main] (Main.java:192) - Saved documents to save file=/tmp/docaudit/dc.kryo

and then

$ java -jar build/libs/DocAudit-1.0.jar --img --file src/test/resources/fw4-image.pdf --savefile /tmp/docaudit/dc.kryo

DEBUG [main] (Main.java:104) - Trying to load Docs
DEBUG [main] (Main.java:147) - Reading Page: 1
DEBUG [main] (Main.java:151) - Comparing to document:fw4.pdf
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (0,0)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(0.0,0.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (283,366)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(282.0,367.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (566,732)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(565.0,733.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (849,1098)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(848.0,1099.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (1132,1464)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(1131.0,1466.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (1415,1830)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(1415.0,1831.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (1698,2196)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(1698.0,2197.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (1981,2562)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(1981.0,2563.0)
DEBUG [main] (CompareDocsImpl.java:128) - Expecting around: (2264,2928)
DEBUG [main] (CompareDocsImpl.java:129) - Found result for Template at(2264.0,2930.0)
DEBUG [main] (CompareDocsImpl.java:143) - Total Score: 0.9996136019610626
DEBUG [main] (Main.java:158) - Image Compare Score Page 1 to page: 1: 0.9993004164490307
INFO [main] (Main.java:162) - Highest score:0.9993004164490307 Found on fw4.pdf page: 1
INFO [main] (Main.java:166) - Found Match to: fw4.pdf:1 score: 0.9993004164490307 (Perfect Score 1.0)


</pre></code>
