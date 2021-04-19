# Context Aware Image Resize

A toy implementation of the [Seam Carving algorithm](https://perso.crans.org/frenoy/matlab2012/seamcarving.pdf) in Scala, based on the blog post ["Content-aware image resizing in JavaScript"](https://trekhleb.dev/blog/2021/content-aware-image-resizing-in-javascript/)

Supports both downscaling and upscaling (up to 2x).

For simplicity, only PPM images are supported.

Usage:

`java -jar resize.jar [-w width] [-h height] <filename>`

or

`./resize [-w width] [-h height] <filename>`

Where the width and height are a number between 0.0 and 2.0, representing how much to scale that size (e.g. `-w 0.5 -h 2.0` will resize a 640 x 480 image to 320 960).
