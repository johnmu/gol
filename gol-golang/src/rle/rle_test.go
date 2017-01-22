package rle

import (
	"bytes"
	"gol"
	"reflect"
	"testing"
)

func Test_parseLength(t *testing.T) {
	type args struct {
		buffer bytes.Buffer
	}

	var buffer1 bytes.Buffer
	buffer1.WriteString("10")

	tests := []struct {
		name    string
		args    args
		want    int
		wantErr bool
	}{
		{"simple", args{buffer1}, 10, false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := parseLength(tt.args.buffer)
			if (err != nil) != tt.wantErr {
				t.Errorf("parseLength() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if got != tt.want {
				t.Errorf("parseLength() = %v, want %v", got, tt.want)
			}
		})
	}
}

func Test_parseRleLine(t *testing.T) {
	type args struct {
		rleLine bytes.Buffer
	}
	tests := []struct {
		name string
		args args
		want map[gol.Cell]bool
	}{
	// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := parseRleLine(tt.args.rleLine); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("parseRleLine() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestReadRleFile(t *testing.T) {
	type args struct {
		inputGrid string
	}
	tests := []struct {
		name string
		args args
		want map[gol.Cell]bool
	}{
	// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := ReadRleFile(tt.args.inputGrid); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("ReadRleFile() = %v, want %v", got, tt.want)
			}
		})
	}
}
