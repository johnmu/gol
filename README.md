# Conway's Game of Life

Example code of "[game of life](https://en.wikipedia.org/wiki/Conway's_Game_of_Life)" written in various languages.

Each implementation has the same file API

Input are three command line arguments separated by a space

1. Number of generations to run
1. Text file that represents the initial grid
1. Output file name of GIF animation

The text file is formatted as [RLE](http://conwaylife.com/wiki/Rle)

See [LifeWiki](http://conwaylife.com/wiki/Main_Page) for examples of patterns. 

Output to command line is a single number representing the execution time of the main loop.
The other output is a GIF animation of the generations.
