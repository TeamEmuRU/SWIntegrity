# PenguinsSwengFall2017

1) The folder "Ada Files" contains some test ada files created for our programs initial run.
2) The folder "PenguinsSwengFall2017-master" contains the code in Visual C++ for the Windows Application (GUI). This was abandoned when the project needed to be for UNIX systems.
3) The folder "UNIX Softtware Integrity Tester" contains the final version of our project.

To use the tester currently, this is what must be done:

  1) Have a unix/linux system
  2) Have a C++ compiler (I used G++)
  3) Can write code in any editor you feel like
  4) To compile, call "g++ main.cpp AdaWeaknesses.cpp" (you can optionally have another command after this for the output file name)
  5) Call the output file (It defaults to a.out if you don't specify a name).
  
  
This program still needs quite a bit of work. Outside of other language support, the code definitley needs to be refactored heavily and cleaned up. I'm certain some instance of redundant code could be fixed. Also, having a way better than string searching to find a weakness would be nice, but we didn't come across any other way.
