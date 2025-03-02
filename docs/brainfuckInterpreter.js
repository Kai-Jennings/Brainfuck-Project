function runBrainfuck(inputID, outputID) {
            let code = document.getElementById(inputID).value;
            let outputElement = document.getElementById(outputID);

            class BrainFuck {
                constructor(code) {
                    this.code = code.split('');
                    this.tape = [0];
                    this.data_pointer = 0;
                    this.instruction_pointer = 0;
                    this.output = '';
                    this.program();
                }
                
                toString() {
                    let pointer_pos = 0;
                    for (let i = 0; i < this.data_pointer; i++) {
                        pointer_pos += String(this.tape[i]).length + 1;
                    }
                    return "\n" + this.tape.map(String).join(" ") + "\n" + " ".repeat(pointer_pos) + "^";
                }
                
                increment_pointer() {
                    this.data_pointer += 1;
                    if (this.tape.length <= this.data_pointer) {
                        this.tape.push(0);
                    }
                }
                
                decrement_pointer() {
                    this.data_pointer -= 1;
                    if (this.data_pointer < 0) {
                        throw new Error(`Tape pointer decremented below 0`);
                    }
                }
                
                increment_tape() {
                    this.tape[this.data_pointer] += 1;
                }
                
                decrement_tape() {
                    this.tape[this.data_pointer] -= 1;
                }
                
                outputChar() {
                    if (this.tape[this.data_pointer] >= 1 && this.tape[this.data_pointer] <= 0x10FFFF) {
                        this.output += String.fromCharCode(this.tape[this.data_pointer]);
                    } else {
                        throw new Error(`Could not convert ${this.tape[this.data_pointer]} to unicode character`);
                    }
                }
                
                loop_left() {
                    if (this.tape[this.data_pointer] === 0) {
                        let depth = 1;
                        while (depth > 0) {
                            this.instruction_pointer += 1;
                            if (this.instruction_pointer >= this.code.length) {
                                throw new Error(`Unmatched '['`);
                            }
                            if (this.code[this.instruction_pointer] === "[") {
                                depth += 1;
                            } else if (this.code[this.instruction_pointer] === "]") {
                                depth -= 1;
                            }
                        }
                    }
                }

                loop_right() {
                    if (this.tape[this.data_pointer] !== 0) {
                        let depth = 1;
                        while (depth > 0) {
                            this.instruction_pointer -= 1;
                            if (this.instruction_pointer >= this.code.length) {
                                throw new Error(`Unmatched ']'`);
                            }
                            if (this.code[this.instruction_pointer] === "]") {
                                depth += 1;
                            } else if (this.code[this.instruction_pointer] === "[") {
                                depth -= 1;
                            }
                        }
                    }
                }

                program() {
                    while (this.instruction_pointer < this.code.length) {
                        const instruction = this.code[this.instruction_pointer];
                        switch (instruction) {
                            case "+": this.increment_tape(); break;
                            case "-": this.decrement_tape(); break;
                            case ">": this.increment_pointer(); break;
                            case "<": this.decrement_pointer(); break;
                            case "[": this.loop_left(); break;
                            case "]": this.loop_right(); break;
                            case ".": this.outputChar(); break;
                            default: throw new Error(`Unexpected token ${instruction} at index ${this.instruction_pointer}`);
                        }
                        this.instruction_pointer += 1;
                    }
                }
            }

            try {
                const bf = new BrainFuck(code);
                outputElement.textContent = bf.output || "No output generated";
                outputElement.style.color = "black";
            } catch (error) {
                outputElement.textContent = "Error: " + error.message;
                outputElement.style.color = "red";
            }
        }
