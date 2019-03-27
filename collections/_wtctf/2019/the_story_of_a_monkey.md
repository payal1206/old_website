---
layout: post
date: 2019-03-25
title: The Story of A Monkey
category: wtctf19
author: neko3
---

### Challenge type
forensics

### Description

We've got a lovely bed-time story for you, just in a different format.

[the_story_of_a_monkey.pcap]({{ site.url }}/assets/ctf-files/wtctf19/the_story_of_a_monkey.pcap)

### Challenge solve

Opening the file in [Wireshark](https://www.wireshark.org/), we can see the file contains a USB capture between a host and one device (`1.22.1`). We can also see the leftover capture data, which is the actual data transmitted by the device. 

![story_of_a_monkey_wireshark]({{ site.url }}/assets/ctf-files/wtctf19/the_story_of_a_monkey_wireshark.png)

We are going to use the `pyshark` python module to extract only the information we need. We'll first load the capture file with a filter on the lenght of the packet (notice packets with leftover data have a length of 72). Then we're going to extract all the data, for further processing. You can see the packets we'll be acting on in Wireshark by applying the filter:

`((usb.transfer_type == 0x01) && (frame.len == 72)) && !(usb.capdata == 00:00:00:00:00:00:00:00)`

By looking at the data, we can make a good guess that this is a keyboard capture. Which means we'll have to decode each byte as a specific key press on a keyboard. There are plenty of resources regarding mapping keypresses, we took one and kept fine-tunning it until it made decent sense.

The output looks like this:

{% highlight shell %}
No map found for this value: 74
No map found for this value: 77
No map found for this value: 74
No map found for this value: 77
touch  sstory
vimm   sst
i# thee cclleeverr  monkey

onnccee   aaauppoon aa  ttiimmee,,   ttheree   wwwaass  aa  cleverr  monkkeey.  hhee  liivvedd   ion  aaa  bbeeeaautiifful iissllaanndd, in an applleee  tree. onee  ddaaayy,  aaa  crocodilleee   sswwaam to  tthheee  islanndd.  SHIFT i SHIFT m hunnggrryy SHIFT , hheee  ssaaaiid.

sso  ttthhee  monkey threewww  aa   rredd   aapplleee  ttooo   tthheee  croccoodillee..  thheee  crocodile munchedd   aannddd  munched. tthheee  nneext ddaaayy, thheee  crocodilleee  camee  bbaack.  SHIFT  SHIFT pplleease, mmaay ii   hhaavveee  two  aappllees/ SHIFT  hheee  asked. hhee  ato ESC dwi ESC lllhddwwi  aatee  onneee  annddd  gave onneee  to hiisss  wife.

tthee  coorocodil  ee  wweentt   ttoo  seee   tthee   mmonkeyy eevveeryy  ddaaayy,  ttoo  liissten too  his takkeess  annddd  weeeaat hiisss  appllees. ESC 4w4w4w2wdwitalleess   ESC i hee  wwaannttedd  too  bbeee  clever, juusstt  likkeee  tthhee  monkey. tthheee  crocodillee SHIFT  SHIFT sss  wifee  hhaad an ideeaa.  SHIFT wwhy don SHIFT  SHIFT ttt  you eeaatt  hiisss  hheeeaart/ then you SHIFT ll bbeee  clever, juusstt  likkeee  him11 SHIFT 

afnom{but_the_monkey_wwaas_smarteerr_anndd ESC ddothee  crocodilleee  wweentt   ttoo  seee   tthee  monkkeey evveeryy  ddaaayy, ESC pkddddothheee  nneext ddaay, hheee  ssaaiiddd  ttooo   tthee  monkey,  SHIFT  SHIFT ccomee  ttooo  my houusse3313 wwwee SHIFT ll havee  lunncch toggeether,,  to thank yuuou  ffoorrr  youurrr  appllees. SHIFT 

butt  wwhhheenn  hheee  arrived, thheee  crocoddiilleee  snapppeedd   aannddd   sssaaaiiidd,  SHIFT monkey1 i wwaannttt  toeeeaatt  youurrr  hheeeaar, so i  caann bbeee   aass  clevveerr  ass  you1 SHIFT 
 ESC pi_climbbeedd  upp  aa  tree3_up_a_tree3313} ESC  ESC  ESC x
mmvvv  ststtoorryy.md
{% endhighlight %}

We can see someone used vim to type a lot of text. In the text we can see the beginning of the flag being typed: `afnom{but_the_monkey_wwaas_smarteerr_anndd`, followed by `dd` for deleting the line.

At the end of the text, we can see `p` being pressed for paste, followed by `i` for insert -- and the rest of the flag: `_climbbeedd  upp  aa  tree3_up_a_tree3313}`.

We can deduce the flag was:

`afnom{but_the_monkey_was_smarter_and_climbed_up_a_tree}`

Python code:

{% highlight python %}
#!/usr/bin/python2
import pyshark
import string
import sys

def get_data(file_name):
    cap = pyshark.FileCapture(file_name, display_filter='frame.len == 72')

    keystroke_data = []
    for _packet in cap:
        try:
            cap_data = _packet.data.usb_capdata
            if cap_data != '00:00:00:00:00:00:00:00':
                keystroke_data.append(cap_data.replace(':', ''))
        except AttributeError:
            pass
    return keystroke_data

def map_keystrokes(keystroke_data):
    newmap = {
     2: "PostFail",
     4: "a",
     5: "b",
     6: "c",
     7: "d",
     8: "e",
     9: "f",
     10: "g",
     11: "h",
     12: "i",
     13: "j",
     14: "k",
     15: "l",
     16: "m",
     17: "n",
     18: "o",
     19: "p",
     20: "q",
     21: "r",
     22: "s",
     23: "t",
     24: "u",
     25: "v",
     26: "w",
     27: "x",
     28: "y",
     29: "z",
     30: "1",
     31: "2",
     32: "3",
     33: "4",
     34: "5",
     35: "6",
     36: "7",
     37: "8",
     38: "9",
     39: "0",
     40: "Enter",
     41: "esc",
     42: "del",
     43: "tab",
     44: "space",
     45: "_",
     47: "{",
     48: "}",
     50: "#",
     51: "esc",
     52: 'Shift',
     54: ",",
     55: '.',
     56: "/",
     57: "CapsLock",
     79: "RightArrow",
     80: "LetfArrow"
     }

    key_presses = []
    i = 1
    for line in keystroke_data:
        bytesArray = bytearray.fromhex(line.strip())
        for byte in bytesArray:
            if byte != 0:
                keyVal = int(byte)
                if keyVal in newmap:
                    key_presses.append(newmap[keyVal])
                else:
                    print("No map found for this value: " + str(keyVal))

        i+=1

    mstr = [' ' for _i in range(len(key_presses))]
    str_counter = 0
    for _keypress in key_presses:
        if _keypress in string.printable:
            mstr[str_counter] = _keypress
            str_counter += 1
        elif _keypress == 'space':
            mstr[str_counter] == ''
            str_counter += 1
        elif _keypress == 'del':
            str_counter -=1
        elif _keypress == 'Enter':
            mstr[str_counter] = '\n'
            str_counter += 1
        elif _keypress == 'Shift':
            mstr[str_counter] = ' SHIFT '
            str_counter += 1
        elif _keypress == 'esc':
            mstr[str_counter] = ' ESC '
            str_counter += 1

    print(''.join(mstr))

if __name__ == '__main__':
    file_name = sys.argv[1]
    keystroke_data = get_data(file_name)
    map_keystrokes(keystroke_data)

{% endhighlight %}

--- 
#### Other useful resources:
* [kaizen-ctf 2018 - Reverse Engineer usb keystrokes from pcap file](https://medium.com/@ali.bawazeeer/kaizen-ctf-2018-reverse-engineer-usb-keystrok-from-pcap-file-2412351679f4)
* [https://wiki.wireshark.org/CaptureSetup/USB](Wireshark USB capture setup)