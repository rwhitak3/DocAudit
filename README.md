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
java -jar build/libs/DocAudit-1.0.jar -learn -img -file src/test/resources/fw4.pdf
and 
java -jar build/libs/DocAudit-1.0.jar -img -file src/test/resources/fw4-image.pdf
</pre></code>
