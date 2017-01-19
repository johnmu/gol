package main

import (
	"os"
	"log"
	"fmt"
	"bufio"
)

func main() {
	args := os.Args[1:]
	if len(args) != 3{
		log.Println("Incorrect number of arguments, please see ../README.md for details")
		os.Exit(1)
	}

	//numGenerations := args[0]
	inputGrid := args[1]
	//outputGif := args[2]

	// Parse the RLE file
	{
		rleFile, err := os.Open(inputGrid)
		if err != nil {
			fmt.Printf("error opening file: %v\n",err)
			os.Exit(1)
		}

		rleScanner := bufio.NewScanner(rleFile)

		for rleScanner.Scan() {
			fmt.Println(rleScanner.Text())
		}

		if err := rleScanner.Err(); err != nil {
			log.Fatal(err)
		}
	}

	// Run iterations

	// output the GIF
}
