---
layout: post
title: "John the Referee"
date: 2015-07-12
ctf: PoliCTF 2015
author: neko3 and xorpse
---

We are given a website with tshirts and asked to find the 'special' one. You can make queries to search for a specific tshirt name and, by looking at the url of the ones already presented, we saw ID 9 was skipped.

We figured out that the search queries were encrypted. We saw that by changing one byte of the ciphertext in the query, the plaintext changes as well. We fiddled around and attempted to manipulate the ciphertext and view what single increments of bytes produce as a result.

When we tried to perform a SQL injection but noticed that `'` gets quoted to `\'` in the output. To perform the injection we needed to replace `\` with any other character.

Together with the knowledge that the item with ID 9 was missing, we constructed a query to retrieve it:

{% highlight sql %}
aa ' or id=9 -- aa
{% endhighlight %}

We changed the `\'` in the output to any other value by incrementing the 4th byte (corresponding to `\` ) by 1 and we got the flag:

{% highlight console %}
flag{Damn_John!_CBC_1s_not_the_best_s0lution_in_this_c4se}
{% endhighlight %}