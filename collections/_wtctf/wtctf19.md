---
layout: post
title: WhatTheCTF 2019
date: 2019-03-25
category: index
permalink: /wtctf19/
logo: wtctf19.png
---

## Event

On 23rd of March 2019 we have organised our first CTF ever! 

We've had a blast organising it, and we hope all our participants enjoyed it too!

## Writeups

We would like to provide official writeups for some of the challenges. Let us know if you made good progress on a particular challenge but didn't manage to solve it, and you'd like to see a writeup. We can add it! If you solved any of our challenges in a different way, we'd be happy to add your writeup to the list, so submit a pull request on the repository, or send us an <a href='mailto&#58;&#99;h&#97;%6Fs%40&#37;61&#102;%&#54;Eo&#37;6&#68;&#46;net'>email</a> or a [tweet](https://twitter.com/uob_afnom)! 

### Official writeups
<table class="table-fixed table-striped">
  <thead>
    <td> Challenge </td>
    <td> Writeup author </td>
  </thead>
{% for writeup in site.wtctf %}
{% if writeup.category == "wtctf19" %}
  <tr>
    <td><a href="{{ writeup.url | prepend: site.url }}" target="_new">{{ writeup.title }}</a></td>
    <td>{{ writeup.author }}</td>
  </tr>
{% endif %}
{% endfor %}
</table>


## Photo Gallery

### Graeme Brainwood
These photos have been provided courtesy of [Graeme Braidwood](https://www.graemebraidwood.com/).

{% include image-gallery.html folder="/assets/photos/wtctf-graeme19" %}

### AFNOM photos

{% include image-gallery.html folder="/assets/photos/wtctf19" %}
