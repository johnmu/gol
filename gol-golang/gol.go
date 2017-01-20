package main

import (
	"os"
	"log"
	"fmt"
	"bufio"
	"strings"
	"unicode"
	"bytes"
	"strconv"
)

type Cell struct {
	x int
	y int
}

func parseLength(buffer bytes.Buffer) (int, error) {
	if buffer.Len() == 0 {
		return 1, nil
	} else {
		return strconv.Atoi(buffer.String())
	}
}

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
	rleLine := readRleFile(inputGrid)
	log.Println(rleLine.String())
	
	cells := parseRleLine(rleLine)
	log.Println(cells)
	
	// Run iterations
	
	// output the GIF
}
func parseRleLine(rleLine bytes.Buffer) map[Cell]bool {
	cells := make(map[Cell]bool)
	var currCount bytes.Buffer
	currX, currY := 0, 0
	
	for c, e := rleLine.ReadByte(); e == nil; c, e = rleLine.ReadByte() {
		count, err := parseLength(currCount)
		if err != nil {
			panic(fmt.Sprintf("Invalid count encountered: %v", currCount.String()))
		}
		
		switch unicode.ToLower(rune(c)) {
		case 'b':
			currX += count
			currCount.Reset()
		case 'o':
			for i := 0; i < count; i++ {
				cells[Cell{currX, currY}] = true
				currX++
			}
			currCount.Reset()
		case '$':
			currX = 0
			currY += count
			currCount.Reset()
		case '!':
			break
		default:
			if unicode.IsDigit(rune(c)) {
				currCount.WriteByte(c)
			} else {
				panic(fmt.Sprintf("Bad RLE string in file: %v", rleLine))
			}
		}
	}
	
	return cells
}
func readRleFile(inputGrid string) bytes.Buffer {
	var rleLine bytes.Buffer
	rleFile, err := os.Open(inputGrid)
	if err != nil {
		panic(fmt.Sprintf("error opening file: %v\n", err))
	}
	
	rleScanner := bufio.NewScanner(rleFile)
	
	for rleScanner.Scan() {
		line := strings.TrimSpace(rleScanner.Text())
		if len(line) == 0 {
			continue
		}
		
		start := line[0]
		
		if unicode.IsDigit(rune(start)) || start == 'b' || start == 'o' || start == '$' || start == '!' {
			rleLine.WriteString(line)
		}
	}
	
	if err := rleScanner.Err(); err != nil {
		panic(err)
	}
	return rleLine
}
