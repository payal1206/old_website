---
layout: post
date: 2019-03-28
title: magic
category: wtctf19
author: hendo
---

### Challenge type
forensics / steg

### Description

Asked a file magician for our flag, he gave us this and laughed.

[unsuspicious]({{ site.url }}/assets/ctf-files/wtctf19/magic_unsuspicious.jpg){:target="_blank"}

### Challenge solve

First let's go through the standard steg image analysis techniques

`exiftool` doesn't give much obvious except a warning field, which is interesting

`Warning                         : [minor] Trailer data after PNG IEND chunk`

This suggests something funny is going on with this file's data.

So let's look up the details of a PNG:
http://www.libpng.org/pub/png/spec/1.2/PNG-Structure.html

This says PNGs begin with a 0x89 0x50 0x4e 0x47 (.PNG in string form), followed by the string IHDR and end with IEND.

`xxd` is a good command line hex viewer for examining files so lets use that.
we get:
{% highlight SHELL %}
00000000: 8950 4e47 0d0a 1a0a 0000 000d 4948 4452  .PNG........IHDR
00000010: 0000 01f4 0000 01fb 0803 0000 000d 5e9d  ..............^.
00000020: 6d00 0000 0467 414d 4100 00b1 8f0b fc61  m....gAMA......a
{% endhighlight %}

at the start, but at the end we get this:

{% highlight SHELL %}
00021720: fd1f cca6 534b 0133 f6c3 0000 0000 4945  ....SK.3......IE
00021730: 4e44 ae42 6082 504b 0102 1e03 0a00 0000  ND.B`.PK........
00021740: 0000 229d 544e 9170 7362 320b 0000 320b  ..".TN.psb2...2.
00021750: 0000 0800 1800 0000 0000 0000 0000 b481  ................
00021760: 0000 0000 666c 6167 2e70 6e67 5554 0500  ....flag.pngUT..
00021770: 034f ad6d 5c75 780b 0001 04e8 0300 0004  .O.m\ux.........
00021780: e803 0000 504b 0506 0000 0000 0100 0100  ....PK..........
00021790: 4e00 0000 740b 0000 0000                 N...t.....

{% endhighlight %}


Now that's interesting, there is an IEND in there but a reference to flag.png as well. This sounds like what we're looking for.

Now we can use `strings` to get a quick overview of the readable strings from the file, it'll give us a quicker overview of the file than scrolling through xxd.

Somewhere near the end we can see this:

{% highlight SHELL %}
d23P
IEND
psb2
flag.pngUT	
m\ux
IHDR
bKGD

{% endhighlight %}

This appears to be the end of one image and the start of another called flag.png.

So at this point we can try extracting the 2nd image. To do this we need to know where the 2nd image is. Using grep we can search for the .PNG in xxd:

{% highlight SHELL %}
$ xxd unsuspicious.png | grep PNG

00000000: 8950 4e47 0d0a 1a0a 0000 000d 4948 4452  .PNG........IHDR
00020c00: e803 0000 8950 4e47 0d0a 1a0a 0000 000d  .....PNG........

{% endhighlight %}

The address on the left shows the offset at the start of that row of string output

if we take the address 0x00020c00 and add bytes we get the address **0x00020c04**.
Converting this from hex tells us the 2nd image is **134148** bytes into the file.


Using the linux **dd** tool we can read data from the image and out to another file. we can specify the starting offset using the skip argument. This will copy the bytes from that point on into a new file. This will carve the 2nd image from the original into a new image file.

`dd if=unsuspicious.png of=answer.png skip=134148 bs=1`

Where:

**if**:    input file
**of**:    output file
**skip**:  number of blocks to skip
**bs**:    number of bytes in a block

we should now have an image called answer.png in our directory.

But viewing it gives us an empty white image?

[hidden_flag]({{ site.url }}/assets/ctf-files/wtctf19/magic_hidden_flag.png){:target="_blank"}

Using our usual steganography / forensics tools on this reveals nothing odd, so let's open it in an image editor as a last resort.

If we increase the contrast and decrease brightness the sneaky trick is revealed:
The flag has been written in a colour 1 bit off white.

[revealed_flag]({{ site.url }}/assets/ctf-files/wtctf19/magic_revealed_flag.png){:target="_blank"}

Our flag is `AFNOM{invisible_ink}`!

### General Steganography / image forensics techniques:

 - strings
 - hex editor like xxd, bless, hexedit
 - exiftool
 - Least significant bit encoding

Tools for file carving:
 - https://github.com/sleuthkit/scalpel
 - https://www.hackingarticles.in/forensic-data-carving-using-foremost/
 - https://github.com/ReFirmLabs/binwalk

After exhausting those move on to messing with the colours / contrast of an image to see if anything's encoded there.
---