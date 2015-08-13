---
layout: post
title: "John the Traveller"
date: 2015-07-12
ctf: PoliCTF 2015
author: neko3 and xorpse
---

The challenge description hints to Finland. We're given a flight booking website which lets us query for European capitals destinations.

For any other capital searched, the query returns a price in EUR. For Helsinki, it returns a price in **px**, which makes us think of pixels.

Also, the query for Helsinki is the only one which returns completed **td class** in the table HTML (classes start with "w").

Therefore, looking in the bootstrap CSS file (get it [here](afnom.net/assets/2015/PoliCTF/jtrav-bootstrap.css)), we find an 'if' statement conditioned on the class starting with 'w' and browser width between 620px and 640px.

![bootstrap css](afnom.net/assets/2015/PoliCTF/traveller-bootstrap.png)

On resize, a QR code appears, which reveals the flag.

{% highlight console %}
flag{run_to_the_hills_run_for_your_life}
{% endhighlight %}
