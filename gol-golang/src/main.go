package main

import (
	"fmt"
	"gol"
	"log"
	"os"
	"rle"
	"strconv"
)

func main() {
	args := os.Args[1:]
	if len(args) != 3 {
		log.Println("Incorrect number of arguments, please see ../README.md for details")
		os.Exit(1)
	}

	numGenerations, err := strconv.Atoi(args[0])
	if err != nil || numGenerations < 1 {
		panic(fmt.Sprintf("Number of generations needs to be a positive integer: %v", args[0]))
	}

	inputGrid := args[1]
	//outputGif := args[2]

	// Parse the RLE file
	cells := rle.ReadRleFile(inputGrid)
	log.Println(cells)

	// Run iterations
	generations := make([]map[gol.Cell]bool, 0, numGenerations)
	generations = append(generations, cells)
	{

	}
	log.Println(generations)

	// output the GIF
}
