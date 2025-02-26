class BrainFuck:
    def __init__(self, code):
        self.code = list(code)
        self.tape = [0]
        self.data_pointer = 0
        self.instruction_pointer = 0
        self.program()
    
    def __str__(self):
        pointer_pos = sum(len(str(self.tape[i])) + 1 for i in range(self.data_pointer))
        return "\n" + " ".join(map(str, self.tape)) + "\n" + " " * pointer_pos + "^"
    
    def increment_pointer(self):
        self.data_pointer += 1
        if len(self.tape) <= self.data_pointer:
            self.tape.append(0)
    
    def decrement_pointer(self):
        self.data_pointer -= 1
    
    def increment_tape(self):
        self.tape[self.data_pointer] += 1
    
    def decrement_tape(self):
        self.tape[self.data_pointer] -= 1
    
    def output(self):
        print(chr(self.tape[self.data_pointer]), end="")
    
    def loop_left(self):
        if self.tape[self.data_pointer] == 0:
            depth = 1
            while depth > 0:
                self.instruction_pointer += 1
                if self.instruction_pointer >= len(self.code):
                    raise ValueError("Unmatched '['")
                if self.code[self.instruction_pointer] == "[":
                    depth += 1
                elif self.code[self.instruction_pointer] == "]":
                    depth -= 1


    def loop_right(self):
        if self.tape[self.data_pointer] != 0:
            depth = 1
            while depth > 0:
                self.instruction_pointer -= 1
                if self.instruction_pointer >= len(self.code):
                    raise ValueError("Unmatched ']'")
                if self.code[self.instruction_pointer] == "]":
                    depth += 1
                elif self.code[self.instruction_pointer] == "[":
                    depth -= 1

    def program(self):
        while self.instruction_pointer < len(self.code):
            instruction = self.code[self.instruction_pointer]
            match instruction:
                case "+": self.increment_tape()
                case "-": self.decrement_tape()
                case ">": self.increment_pointer()
                case "<": self.decrement_pointer()
                case "[": self.loop_left()
                case "]": self.loop_right()
                case ".": self.output()
                case _: raise ValueError(f"Unexpected token '{instruction}' at index {self.instruction_pointer}")
            self.instruction_pointer += 1

a = "++++++++[>+++++++++<-]>.<+++++[>++++++<-]>-.+++++++..+++.<++++++[>-----------<-]>-.------------.<++++++++[>+++++++++++<-]>-.--------.+++.------.--------.<++++++[>-----------<-]>-.<+++++[>-----<-]>++.<"
BF = BrainFuck(a.replace("\n", ""))
print(BF)
