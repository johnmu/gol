package main

import (
	"os"
	"log"
	"rle"
)

func main() {
	args := os.Args[1:]
	if len(args) != 3 {
		log.Println("Incorrect number of arguments, please see ../README.md for details")
		os.Exit(1)
	}
	
	//numGenerations := args[0]
	inputGrid := args[1]
	//outputGif := args[2]
	
	// Parse the RLE file
	rleLine := rle.ReadRleFile(inputGrid)
	log.Println(rleLine.String())
	
	cells := rle.ParseRleLine(rleLine)
	log.Println(cells)
	
	// Run iterations
	
	// output the GIF
}
