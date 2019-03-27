---
layout: post
date: 2019-03-25
title: Social Monkeys
category: wtctf19
author: neko3
---

### Challenge type
steg

### Description

> Working hard to hide flags...
>
> [social_monkeys.jpg]({{ site.url }}/assets/ctf-files/wtctf19/social_monkeys.jpg){:target="_blank"}

### Challenge solve

The first step in solving the challenge is running `exiftool` on the image. Exiftool is a handy tool for viewing the metadata of an image. It can also be used for writing metadata.

{% highlight shell %}

$> exiftool social_monkeys.jpg
ExifTool Version Number         : 11.11
File Name                       : social_monkeys.jpg
Directory                       : .
File Size                       : 3.9 MB
File Modification Date/Time     : 2019:03:25 14:11:19+00:00
File Access Date/Time           : 2019:03:25 14:11:19+00:00
File Inode Change Date/Time     : 2019:03:25 14:11:19+00:00
File Permissions                : rw-r--r--
File Type                       : JPEG
File Type Extension             : jpg
MIME Type                       : image/jpeg
Exif Byte Order                 : Little-endian (Intel, II)
Make                            : Google
Camera Model Name               : Pixel 2
Orientation                     : Horizontal (normal)
X Resolution                    : 72
Y Resolution                    : 72
Resolution Unit                 : inches
Software                        : HDR+ 1.0.220943774z
Modify Date                     : 2019:03:13 16:50:51
Y Cb Cr Positioning             : Centered
Copyright                       : 9pgoYQoPBDWtbbquB4RCYmoX9BXq9XRKeGb8x
Exposure Time                   : 1/111
F Number                        : 1.8
Exposure Program                : Program AE
ISO                             : 129
Exif Version                    : 0220
Date/Time Original              : 2019:03:13 16:50:51
Create Date                     : 2019:03:13 16:50:51
Components Configuration        : Y, Cb, Cr, -
Shutter Speed Value             : 1/111
Aperture Value                  : 1.8
Brightness Value                : 3.13
Exposure Compensation           : 0
Max Aperture Value              : 1.8
Subject Distance                : 1.056 m
Metering Mode                   : Center-weighted average
Flash                           : Off, Did not fire
Focal Length                    : 4.4 mm
Warning                         : [minor] Unrecognized MakerNotes
Sub Sec Time                    : 163442
Sub Sec Time Original           : 163442
Sub Sec Time Digitized          : 163442
Flashpix Version                : 0100
Color Space                     : sRGB
Exif Image Width                : 4032
Exif Image Height               : 3024
Interoperability Index          : R98 - DCF basic file (sRGB)
Interoperability Version        : 0100
Sensing Method                  : One-chip color area
Scene Type                      : Directly photographed
Custom Rendered                 : Custom
Exposure Mode                   : Auto
White Balance                   : Auto
Digital Zoom Ratio              : 0
Focal Length In 35mm Format     : 27 mm
Scene Capture Type              : Standard
Contrast                        : Normal
Saturation                      : Normal
Sharpness                       : Normal
Subject Distance Range          : Close
Compression                     : JPEG (old-style)
Thumbnail Offset                : 26754
Thumbnail Length                : 8868
JFIF Version                    : 1.02
Image Width                     : 4032
Image Height                    : 3024
Encoding Process                : Baseline DCT, Huffman coding
Bits Per Sample                 : 8
Color Components                : 3
Y Cb Cr Sub Sampling            : YCbCr4:2:0 (2 2)
Aperture                        : 1.8
Image Size                      : 4032x3024
Megapixels                      : 12.2
Scale Factor To 35 mm Equivalent: 6.1
Shutter Speed                   : 1/111
Create Date                     : 2019:03:13 16:50:51.163442
Date/Time Original              : 2019:03:13 16:50:51.163442
Modify Date                     : 2019:03:13 16:50:51.163442
Thumbnail Image                 : (Binary data 8868 bytes, use -b option to extract)
Circle Of Confusion             : 0.005 mm
Depth Of Field                  : 1.29 m (0.72 - 2.01 m)
Field Of View                   : 67.4 deg
Focal Length                    : 4.4 mm (35 mm equivalent: 27.0 mm)
Hyperfocal Distance             : 2.22 m
Light Value                     : 8.1

{% endhighlight %}

This reveals an odd value in the `Copyright` field: `9pgoYQoPBDWtbbquB4RCYmoX9BXq9XRKeGb8x`. 

Using [CyberChef](https://gchq.github.io/CyberChef), we can try to guess what encoding could have been used to create the string. Or we can use the `Magic` function in CyberChef, which will try to guess the encoding for us.

Magic correctly detects that the string is **base58** encoded, and shows us the decoded string:

`AFN0M{h4rd_4t_w0rk_m0nk3y2}`

---

#### Other useful resources:
* [Base What? A Practical Introduction to Base Encoding](https://code.tutsplus.com/tutorials/base-what-a-practical-introduction-to-base-encoding--net-27590)
* [exiftool](https://www.sno.phy.queensu.ca/~phil/exiftool/)
