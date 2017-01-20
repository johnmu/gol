package rle

import (
	"bufio"
	"bytes"
	"fmt"
	"os"
	"gol"
	"strconv"
	"strings"
	"unicode"
)

func ParseLength(buffer bytes.Buffer) (int, error) {
	if buffer.Len() == 0 {
		return 1, nil
	} else {
		return strconv.Atoi(buffer.String())
	}
}

func ParseRleLine(rleLine bytes.Buffer) map[gol.Cell]bool {
	cells := make(map[gol.Cell]bool)
	var currCount bytes.Buffer
	currX, currY := 0, 0

	for c, e := rleLine.ReadByte(); e == nil; c, e = rleLine.ReadByte() {
		count, err := ParseLength(currCount)
		if err != nil {
			panic(fmt.Sprintf("Invalid count encountered: %v", currCount.String()))
		}

		switch unicode.ToLower(rune(c)) {
		case 'b':
			currX += count
			currCount.Reset()
		case 'o':
			for i := 0; i < count; i++ {
				cells[gol.Cell{currX, currY}] = true
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

func ReadRleFile(inputGrid string) bytes.Buffer {
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
