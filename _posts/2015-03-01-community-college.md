---
layout: post
title: "Community College"
date: 2015-03-01
ctf: Boston Key Party 2015
author: xorpse
ext-url: http://xorp.se/ctf/2015/03/01/boston-key-party-2015-community-college/
---

The binary is a standard 32-bit ELF for MIPS, confirmed by use of `file`:

{% highlight console %}
$ file fredkin.nice.15e4d62a4782b29e0ebcfcd3cb88b71d
fredkin.nice.15e4d62a4782b29e0ebcfcd3cb88b71d: ELF 32-bit LSB executable, MIPS, MIPS-II version 1 (SYSV), dynamically linked (uses shared libs), for GNU/Linux 2.6.32, BuildID[sha1]=2204905041c5ddc8a13ab816a6421daf0870b6a0, stripped
{% endhighlight %}

## Analysis with IDA Pro

After locating the `main` function we see that the executable takes a single command
line argument, which should be the flag:

{% highlight console %}
lw      $v1, 0x70+argc($fp)
li      $v0, 2
beq     $v1, $v0, loc_404A14
{% endhighlight %}

The input is first converted into a list of binary integers, character-by-character:

{% highlight console %}
loc_404A48:
lw      $v0, 0x70+argv($fp)
addiu   $v0, 4
lw      $v1, 0($v0)
lw      $v0, 0x70+counter0($fp)
addu    $v0, $v1, $v0
lb      $v0, 0($v0)
Feed input string byte-by-byte
move    $a0, $v0
jal     charToBinaryToBuffer
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
lw      $v0, 0x70+counter0($fp)
addiu   $v0, 1
sw      $v0, 0x70+counter0($fp)
{% endhighlight %}

This list is then transformed into a vector of `Wires` objects; something akin to the following C structure:

{% highlight c %}
struct wires {
  int c;
  int o1;
  int o2;
}
{% endhighlight %}

Binary digits are grouped into threes and converted to `Wires` objects:

{% highlight console %}
loc_404B38:
lui     $v0, 0x42
addiu   $a0, $v0, (var0 - 0x420000)
lw      $v0, -0x7FC4($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
lw      $v0, 0($v0)
sw      $v0, 0x70+tempWire0($fp)
lui     $v0, 0x42
addiu   $a0, $v0, (var0 - 0x420000)
lw      $v0, -0x7FBC($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
lui     $v0, 0x42
addiu   $a0, $v0, (var0 - 0x420000)
lw      $v0, -0x7FC4($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
lw      $v0, 0($v0)
sw      $v0, 0x70+tempWire1($fp)
lui     $v0, 0x42
addiu   $a0, $v0, (var0 - 0x420000)
lw      $v0, -0x7FBC($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
lui     $v0, 0x42
addiu   $a0, $v0, (var0 - 0x420000)
lw      $v0, -0x7FC4($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
lw      $v0, 0($v0)
sw      $v0, 0x70+tempWire2($fp)
lui     $v0, 0x42
addiu   $a0, $v0, (var0 - 0x420000)
lw      $v0, -0x7FBC($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
addiu   $v0, $fp, 0x70+wire0
move    $a0, $v0
lw      $a1, 0x70+tempWire0($fp)
lw      $a2, 0x70+tempWire1($fp)
lw      $a3, 0x70+tempWire2($fp)
lw      $v0, -0x7FD0($gp) ; new Wire(tempWire0, tempWire1, tempWire2)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
addiu   $v1, $fp, 0x70+wireVector
addiu   $v0, $fp, 0x70+wire0
move    $a0, $v1
move    $a1, $v0
lw      $v0, -0x7FA4($gp) ; push_back(wire0) to wireVector
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
{% endhighlight %}

The length of the input when treated as a list of binary digits should be a multiple of 3,
otherwise it's padded with `Wires` with each field set to `0`:

{% highlight console %}
addiu   $v0, $fp, 0x70+wire
move    $a0, $v0
move    $a1, $zero
move    $a2, $zero
move    $a3, $zero
lw      $v0, -0x7FD0($gp) ; new Wire(0, 0, 0)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
addiu   $v1, $fp, 0x70+wireVector
addiu   $v0, $fp, 0x70+wire
move    $a0, $v1
move    $a1, $v0
lw      $v0, -0x7FA4($gp) ; push_back(wire) to wireVector
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
j       loc_404C44
or      $at, $zero
{% endhighlight %}

The result of the list
transformation is a vector of `Wires`, which are then 'shuffled' before being converted
to a string and compared with a hard-coded value; the result of which determining if the
input flag is valid:

{% highlight console %}
addiu   $v1, $fp, 0x70+wireVectorCopy
addiu   $v0, $fp, 0x70+wireVector
move    $a0, $v1
move    $a1, $v0
lw      $v0, -0x7F98($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
addiu   $v0, $fp, 0x70+wireVectorCopy
move    $a0, $v0
jal     isValidFlag
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
move    $s0, $v0
addiu   $v0, $fp, 0x70+wireVectorCopy
move    $a0, $v0
lw      $v0, -0x7FA0($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x70+gpBase($fp)
beqz    $s0, loc_404D04
or      $at, $zero
{% endhighlight %}

The procedure 'isValidFlag' performs both the 'shuffling', conversion to a string and the final comparison:

{% highlight console %}
addiu   $v1, $fp, 0x50+outputVector
addiu   $v0, $fp, 0x50+vector
move    $a0, $v1
move    $a1, $v0
li      $a2, 8196
jal     shuffleWires
or      $at, $zero
lw      $gp, 0x50+gpBase($fp)
addiu   $v0, $fp, 0x50+vector
move    $a0, $v0
lw      $v0, -0x7FA0($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x50+gpBase($fp)
addiu   $v0, $fp, 0x50+outputVector
addiu   $v1, $fp, 0x50+outputVector1
move    $a0, $v1
move    $a1, $v0
lw      $v0, -0x7F98($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x50+gpBase($fp)
addiu   $v0, $fp, 0x50+shuffledWiresString
move    $a0, $v0
addiu   $v0, $fp, 0x50+outputVector1
move    $a1, $v0
jal     wiresToString
or      $at, $zero
lw      $gp, 0x50+gpBase($fp)
addiu   $v0, $fp, 0x50+outputVector1
move    $a0, $v0
lw      $v0, -0x7FA0($gp)
move    $t9, $v0
jalr    $t9
or      $at, $zero
lw      $gp, 0x50+gpBase($fp)
addiu   $v0, $fp, 0x50+shuffledWiresString
move    $a0, $v0
lui     $v0, 0x41
addiu   $a1, $v0, (a00011010010001 - 0x410000)
lw      $v0, -0x7F94($gp) ; Comparison of hard-coded string with shuffledWiresString
{% endhighlight %}

The overall 'shuffle' procedure (omitted) is repeated 8196 times, and is composed of two stages:
field swapping and rotations.

### Field swapping

Swapping consists of mapping each `Wires` object to another by swapping the `o1` and `o2`
fields if the `c` field is not equal to 1. Clearly this process can be reversed by applying
the operation twice.

### Rotation

The entire vector of wires is essentially treated as a single binary number, and a right
rotation by 1 is performed; this may be reversed by rotating left by 1.

## Solution

All that remains is to apply the transformations in reverse to the hard-coded binary string,
treating it as a vector of `Wires` objects. The following OCaml code performs the required
operations:

{% highlight ocaml %}
(* Compile with:
 * ocamlfind opt -o solver solver.ml -package core,threads -linkpkg -thread
 *)

open Core.Std

type wire = {
  c  : int;
  o1 : int;
  o2 : int;
}

let key =
  "000110100100011111000110000110101100011001011011110001001100101010011001101110100010010110100110010001110010110011100101011110000001101101001011111101100000011011000110"

(* Reverse transform 1 *)
let swap_wire_params =
  List.map ~f:(fun w ->
    if w.c <> 1 then w else { w with o1 = w.o2; o2 = w.o1 })

(* Reverse transform 2 *)
let rev_rotate_shuffle wires =
  let wa = Array.of_list wires in
  let len = Array.length wa in
  let rec aux acc n =
    if n < len then
      let c' = wa.(n).o1 in
      let o1' = wa.(n).o2 in
      let o2' = if n = len - 1 then wa.(0).c else wa.(n + 1).c in
      aux ({ c = c'; o1 = o1'; o2 = o2' } :: acc) (n + 1)
    else
      List.rev acc
  in
  aux [] 0

(* Composed transforms 1 & 2 *)
let deshuffle ws = rev_rotate_shuffle ws |> swap_wire_params

(* Compose n combinator *)
let rec times n f x =
  if n <= 0 then
    x
  else
    times (n - 1) f (f x)

(* Buggy char -> binary digit & reverse *)
let c2b c = if c = '1' then 1 else 0
let b2c i = if i = 1 then '1' else '0'

(* String of binary digits to list of wires *)
let bin_str_to_wires str =
  let len = String.length str in
  assert (len mod 3 = 0);
  let rec aux acc n =
    if n < len then
      aux ({ c = c2b str.[n]; o1 = c2b str.[n + 1]; o2 = c2b str.[n + 2] } :: acc) (n + 3)
    else
      List.rev acc
  in
  aux [] 0

(* List of wires to ASCII string; hacky and inefficient *)
let wires_to_ascii ws =
  let bs =
    List.fold_right ws ~init:[]
                    ~f:(fun w xs -> (b2c w.c) :: (b2c w.o1) :: (b2c w.o2) :: xs)
  in
  let buf = Buffer.create 1024 in
  let rec aux xs =
    let b = List.take xs 8 in
    if List.is_empty b then
      Buffer.contents buf
    else (
      Buffer.add_char buf (("0b" ^ String.of_char_list b) |> Int.of_string |> Char.of_int_exn);
      aux (List.drop xs 8)
    )
  in
  aux bs

(* Reverse transform key to get flag *)
let solve input = times 8196 deshuffle (bin_str_to_wires input) |> wires_to_ascii

let () =
  printf "Flag: %s\n" (solve key)
{% endhighlight %}

Upon execution it yields the flag:

{% highlight console %}
$ ./solver
Flag: myheadmateisafredkin!
{% endhighlight %}
