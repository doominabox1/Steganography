## Steganography
GUI program to demonstrate steganography 

In the embed tab, select an image and a file you'd like to hide in the image, then click execute.  
In the detach tab, select an image that has a file hidden inside, then click execute.
  
There are a few limitations to the program, it can only store one file in an image (Although it does accept achieve files like 7zip for example), the the max file size is 33mb due to the way I'm storing data.   

File Embedding:  
[![File Embedding][embed_small]][embed_large]  
  
File Detaching:  
[![File Embedding][detach_small]][detach_large]


In the `Example` folder, there are 3 files. I embedded `Input Image and File.jpg` into itself which results in `Steganography Output.png`. If it looks the exact same then the steganography works. `File after being extracted.jpg` is the input image/file after being extracted. 
 
 
[embed_large]: https://i.imgur.com/xrnJHgj.png
[detach_large]: https://i.imgur.com/coMZxT2.png
[embed_small]: https://i.imgur.com/BZsj9i7.png
[detach_small]: https://i.imgur.com/nPDBLZX.png
