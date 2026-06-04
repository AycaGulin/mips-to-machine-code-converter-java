# MIPS to Machine Code Converter in Java

## Project Description

This project is a Java Swing GUI application that converts MIPS assembly instructions into hexadecimal machine code. The user enters MIPS assembly code into a multi-line text area, clicks the Convert button, and the program displays the corresponding instruction addresses and machine code values.

The project focuses on MIPS instruction encoding, register mapping, label handling, branch offset calculation, jump address calculation, and GUI-based input and output using Java Swing.

## Technologies Used

- Java
- Java Swing
- Java AWT
- MIPS Assembly
- Object-Oriented Programming

## Project Structure

```text
mips-to-machine-code-converter-java
├── .gitignore
├── README.md
└── src
    └── MIPSConverterGUI.java
```

## Features

- GUI-based MIPS assembly input
- Converts supported MIPS instructions into hexadecimal machine code
- Displays instruction addresses starting from `0x00400000`
- Supports labels for branch and jump instructions
- Calculates branch offsets
- Calculates jump target addresses
- Displays results in separate Address and Machine Code output areas
- Uses register names such as `$zero`, `$t0`, `$s0`, `$a0`, and `$v0`

## Supported Instructions

### R-Type Instructions

- `add`
- `sub`
- `and`
- `or`
- `sll`
- `srl`
- `sllv`
- `srlv`

### I-Type Instructions

- `addi`
- `andi`
- `lw`
- `sw`
- `beq`
- `bne`

### J-Type Instructions

- `j`
- `jal`

## Example Input

```text
again:
add $t0,$s3,$v0
addi $s0,$zero,4
beq $a0,$s0,x1
sw $s0,4($t0)
x1:
addi $t0,$t0,4
lw $t6,-4($zero)
bne $t0,$t1,again
```

## Example Output

```text
Address        Machine Code
0x00400000     0x02624020
0x00400004     0x20100004
0x00400008     0x10900001
0x0040000C     0xAD100004
0x00400010     0x21080004
0x00400014     0x8C0EFFFC
0x00400018     0x1509FFF9
```

## How to Compile and Run

### Compile

Open a terminal in the project folder and run:

```bash
javac -d out src/MIPSConverterGUI.java
```

### Run

```bash
java -cp out MIPSConverterGUI
```

## How the Program Works

1. The user enters MIPS assembly instructions into the input text area.
2. The program scans the code and stores labels with their corresponding addresses.
3. Each instruction is parsed according to its instruction type.
4. Register names are converted into their binary register codes.
5. Immediate values, branch offsets, and jump targets are calculated.
6. The 32-bit binary instruction is converted into hexadecimal machine code.
7. The program displays each instruction address and its machine code.

## Notes

- The starting address is `0x00400000`.
- The program outputs hexadecimal machine code.
- Unsupported or invalid instructions are converted to `0x00000000`.
- Labels are used for `beq`, `bne`, `j`, and `jal` instructions.
- The project uses Java Swing and does not require external libraries.

## Purpose of the Project

The purpose of this project is to practice Java GUI programming and understand how MIPS assembly instructions are translated into machine code. It combines computer architecture concepts with practical Java application development.

## Project Outcome

This project demonstrates the ability to build a GUI-based Java application that performs instruction parsing, address calculation, label resolution, and machine code generation for selected MIPS assembly instructions.
