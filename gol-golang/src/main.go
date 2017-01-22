package main

import (
	"fmt"
	"gol"
	"log"
	"os"
	"rle"
	"strconv"
	"sync"
	"time"
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
	start := time.Now()
	generations := make([]map[gol.Cell]bool, 0, numGenerations)
	generations = append(generations, cells)

	for i := 0; i < numGenerations; i++ {
		nextGeneration := make(map[gol.Cell]bool)
		currCells := generations[len(generations)-1]

		var wg sync.WaitGroup
		queue := make(chan []gol.Cell, len(currCells))
		wg.Add(len(currCells))
		for cell := range currCells {
			go func(cell gol.Cell) {
				defer wg.Done()
				queue <- gol.ApplyAllNeighbors(currCells, cell)
			}(cell) // This notation looks funny
		}
		wg.Wait()
		close(queue)
		for liveCells := range queue {
			for _, liveCell := range liveCells {
				nextGeneration[liveCell] = true
			}
		}
		generations = append(generations, nextGeneration)
	}

	log.Println(fmt.Sprintf("%s", time.Since(start)))
	//log.Println(generations)

	// output the GIF
}
