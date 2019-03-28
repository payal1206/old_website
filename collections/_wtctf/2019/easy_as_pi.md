---
layout: post
date: 2019-03-28
title: easy as pi
category: wtctf19
author: cxw602
---

### Challenge type
crypto

### Description

> sir cumference would be so proud...
>
> [regular_old_pie.jpg]({{ site.url }}/assets/ctf-files/wtctf19/regular_old_pie.jpg){:target="_blank"}

### Challenge solve

The starting point for most challenges where we are only provided with an image is to check it for any hidden information.

There are many tools available for this, but a good place to start is with the `strings` command (usually built in on a Unix environment, and easily installable on other platforms). This command returns all strings of printable characters stored in a file.

{% highlight SHELL %}
C:\Users\Connor\Documents\AFNOM>strings -n 5 regular_old_pie.jpg

Strings v2.53 - Search for ANSI and Unicode strings in binary images.
Copyright (C) 1999-2016 Mark Russinovich
Sysinternals - www.sysinternals.com

!1"%)+...
383-7(-.+
%/---+---------------------------------------------
"2QBR
R]H(C
J`L>V
ooyEh
(;DRL
2%gs~
x;m=V
ZjHi_
 Z pC].-f
O2iRJ
xuPv{
UD];$
UvQ`.6;
9&AJ\@q
ELe)|
Yu{s?WX
8j7=Q
HD&6bb~
|u):I
*w.$K.F
jTHps
T,f"l6
B!:V#t:
Wp7"N
&jIu(
m<OHZ_o
70mW5
TTa[A
}~%MKH
b1{u<
tq9[d
wIIsXI^P
v,ah{G
nf:)8
VXL&xk
%n2tE=
"l|JD
\IB2E
N.JT
P>m!T
#8RjI(C
3.150591653679882137363533392178502894197168299365114821964934592306715405275308898537033824442116076881148176413381205738082833709460582132626459
{% endhighlight %}

Another option is to use an online image forensics tool. My go-to would be Forensically:

[Forensically - free online photo forensics tool](https://29a.ch/photo-forensics)

This site has lots of options for analysing images, but for this challenge we only really need to use `String Extraction`.

Using either of these tools, we eventually come across this vaguely familiar number:

`3.150591653679882137363533392178502894197168299365114821964934592306715405275308898537033824442116076881148176413381205738082833709460582132626459`

Given all the challenge clues so far seem to be leading us to one thing, lets compare this string with pi:

`3.150591653679882137363533392178502894197168299365114821964934592306715405275308898537033824442116076881148176413381205738082833709460582132626459`

`3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359`

Now there's a few things we could try here.

Co-ordinates?

[co-ordinates.PNG]({{ site.url }}/assets/ctf-files/wtctf19/co-ordinates.PNG)

Doesn't look particularly relevant.

Maybe finding the difference?

[Big Number Calculator](https://www.calculator.net/big-number-calculator.html?cx=3.150591653679882137363533392178502894197168299365114821964934592306715405275308898537033824442116076881148176413381205738082833709460582132626459&cy=3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284&cp=146&co=minus)

`0.00899900009008889890089000889900000999999889999000900098998999999889899898909989990899899909999900889900008990009889909098898909990999990090109959`

Again, nothing jumps out as us here.

If we think about what this challenge is leading us to, there must be some information encoded in this string somehow. Maybe there is some pattern to the differing digits between this string and pi?

Time to go to python! Let's mark all differences as 0, and all similarities as 1.

{% highlight SHELL %}
C:\Users\Connor>python
Python 3.7.2 (tags/v3.7.2:9a3ffc0492, Dec 23 2018, 23:09:28) [MSC v.1916 64 bit (AMD64)] on win32
Type "help", "copyright", "credits" or "license" for more information.
>>> pi = "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359"
>>> weird = "3.150591653679882137363533392178502894197168299365114821964934592306715405275308898537033824442116076881148176413381205738082833709460582132626459"
>>> print(str(len(pi)) + ":" + str(len(weird)))
146:146
>>> pattern = ""
>>> for i in range(len(weird)):
...     if weird[i] == pi[i]:
...             pattern = pattern + "1"
...     else:
...             pattern = pattern + "0"
...
>>> pattern
'11100110111001000010010001100010111101111110011101100110101101111110010110100011011000110110011110100010111001011010010000100100011001111010010011'
{% endhighlight %}

Aha! That looks like binary, lets head to [CyberChef](https://gchq.github.io/CyberChef) which should be able to decode it.

And our flag is: `æä$b÷çf·å£cg¢å¤$g¤.`

Wait, that doesn't look right. Maybe we should try decoding it the other way round?

{% highlight SHELL %}
>>> pattern
'00011001000110111101101110011101000010000001100010011001010010000001101001011100100111001001100001011101000110100101101111011011100110000101101100'
{% endhighlight %}

`..Û....H.\..].[Û.[.` still doesn't look right. Maybe its a padding issue? Lets try looking at just the fractional-part of the two strings.

`011001000110111101101110011101000010000001100010011001010010000001101001011100100111001001100001011101000110100101101111011011100110000101101100`

Finally, we get our flag: `dont be irrational`

---
