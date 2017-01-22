package main

import (
	"fmt"
	"gol"
	"image"
	"image/color"
	"image/gif"
	"log"
	"os"
	"rle"
	"strconv"
	"sync"
	"time"

	"math"

	"github.com/golang/geo/r2"
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
	outputGif := args[2]

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
	{
		var frames []*image.Paletted
		var delays []int

		// Could be some numerical issues here with large grids
		// TODO Should probably use image.Rectangle
		var points []r2.Point
		for _, generation := range generations {
			for cell := range generation {
				points = append(points, r2.Point{X: float64(cell.X), Y: float64(cell.Y)})
			}
		}

		bounds := r2.RectFromPoints(points...)
		var palette = []color.Color{
			color.RGBA{0x00, 0x00, 0x00, 0xff},
			color.RGBA{0xff, 0xff, 0xff, 0xff},
		}

		for _, generation := range generations {
			img := image.NewPaletted(image.Rect(0, 0, int(math.Ceil(bounds.X.Length())), int(math.Ceil(bounds.Y.Length()))), palette)
			frames = append(frames, img)
			delays = append(delays, 10)
			for cell := range generation {
				img.Set(cell.X, cell.Y, palette[1])
			}
		}

		f, err := os.OpenFile(outputGif, os.O_WRONLY|os.O_CREATE, 0666)
		defer f.Close()
		if err != nil {
			panic(fmt.Sprintf("Cannot create file %v because of %v", outputGif, err))
		}
		gif.EncodeAll(f, &gif.GIF{
			Image: frames,
			Delay: delays,
		})
	}
}
