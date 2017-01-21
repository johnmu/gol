package gol

/*
Cell is simply an (X,Y) coordinate tuple
*/
type Cell struct {
	X int
	Y int
}

/*
Displace retuns a new cell moved by the specified coordicates
*/
func (c Cell) Displace(x int, y int) Cell {
	return Cell{c.X + x, c.Y + y}
}

/*
ApplyAllNeighbors applies a filter to all neighboring cells
including self and returns the live cells
*/
func ApplyAllNeighbors(cells map[Cell]bool, cell Cell) []Cell {
	retCells := make([]Cell, 0, 0)
	for x := -1; x <= 1; x++ {
		for y := -1; y <= 1; y++ {
			currCell := cell.Displace(x, y)
			if _, alive := cells[currCell]; ApplyB3S23Rule(alive, CountLiveNeighbors(cells, currCell)) {
				retCells = append(retCells, currCell)
			}
		}
	}
	return retCells
}

/*
CountLiveNeighbors counts the number of live neighbors to a cell
*/
func CountLiveNeighbors(cells map[Cell]bool, cell Cell) int {
	num := 0
	for x := -1; x <= 1; x++ {
		for y := -1; y <= 1; y++ {
			if _, alive := cells[cell.Displace(x, y)]; !(x == 0 && y == 0) && alive {
				num++
			}
		}
	}
	return num
}

/*
ApplyB3S23Rule applies the standard Conway's rules to game of life
*/
func ApplyB3S23Rule(alive bool, neighborsAlive int) bool {
	if alive {
		if neighborsAlive < 2 || neighborsAlive > 3 {
			return false
		}
		return true
	}
	if neighborsAlive == 3 {
		return true
	}
	return false
}
