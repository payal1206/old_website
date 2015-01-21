---
layout: post
title: "ImageUpload"
date: 2014-10-23
ctf: Hack.lu 2014
author: Break
---

## Description

In the Wild Wild Web, there are really bad guys. The sheriff doesn't know them all. Therefore, he needs your help.
Upload pictures of criminals to this site and help the sheriff to arrest them.
You can make this Wild Wild Web much less wild!!!

Pictures will be deleted on regular basis!

## Solution

ImageUpload was a web based challenge. The website consisted of a Login page and an upload
section which accepted pictures (jpg/jpeg). After uploading a picture some
meta-information of the exif-header of images were displayed (height, width, author,
manufacturer, model).

![./imageupload.png](http://afnom.net/assets/2014/imageupload.png)

After unsuccessfully uploading a php shell (as shell.jpg), we focused on the exif data.

There is a tool in Linux to modify this data: `shell-code`

	exiftool -<EXIF-FIELD>=<DATA> picture.jpg

Our first guesses were PHP and JavaScript since the data gets displayed on the website (see
[[imageupload_pic]](#imageupload_pic) below the picture).

But neither PHP nor JavaScript worked.

We then realised that the website shows an error message, when putting an apostrophe `'`
into the exif data. This means the website is vulnerable to SQL injection attacks.

Unfortunately, it turned out that the chosen exif field was integers-only (other fields were not
displayed correctly) and we had to deal with outputting numbers only.

For the database we guessed the layout as a table "users" with columns "password" and "name".

To prove it, we counted the password column in table users:

	exiftool -model="' + (SELECT COUNT(password) FROM users) + '" logo.jpeg

The resulting output "2" showed us that

* table users exists
* column password exists
* there are two users

We further guessed one user as "sheriff" at the Login page and confirmed that column "name"
exists, too.

To output the sheriff's password in numbers we iterated over the password and printed the ascii
representation of the character. E.g. for the first character in ascii we queried the following:

	exiftool -model="' + (SELECT ASCII(SUBSTR(password, 0, 1)) FROM users WHERE name = 'sheriff') + '" logo.jpeg

We retrieved the sheriff's password as "AO7eikkOCucCFJOyyaaQ" and were able to login to retrieve
the flag `flag{1_5h07_7h3_5h3r1ff}` for 200+70 points.

(joint work Andreea, Joe, Mike)