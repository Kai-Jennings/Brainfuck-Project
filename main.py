class Brainfuck:
    """
    A Brainfuck interpreter that precomputes bracket mappings for efficient loop execution.
    """
    def __init__(self, code: str) -> None:
        # Keep only valid Brainfuck commands.
        valid_commands = {">", "<", "+", "-", ".", "[", "]"}
        self.code = [char for char in code if char in valid_commands]
        self.tape = [0]
        self.dp = 0  # Data pointer.
        self.ip = 0  # Instruction pointer.
        self.bracket_map = self._build_bracket_map()

    def _build_bracket_map(self) -> dict[int, int]:
        """
        Precompute a mapping between matching brackets to optimize loop jumps.
        
        Returns:
            A dictionary mapping the index of each '[' or ']' to its matching bracket.
            
        Raises:
            ValueError: If there is an unmatched bracket.
        """
        bracket_map: dict[int, int] = {}
        stack: list[int] = []
        for pos, cmd in enumerate(self.code):
            if cmd == "[":
                stack.append(pos)
            elif cmd == "]":
                if not stack:
                    raise ValueError(f"Unmatched ']' at position {pos}")
                start = stack.pop()
                bracket_map[start] = pos
                bracket_map[pos] = start
        if stack:
            raise ValueError(f"Unmatched '[' at position {stack.pop()}")
        return bracket_map

    def run(self) -> None:
        """
        Execute the Brainfuck code.
        """
        while self.ip < len(self.code):
            cmd = self.code[self.ip]
            if cmd == ">":
                self.dp += 1
                if self.dp == len(self.tape):
                    self.tape.append(0)
            elif cmd == "<":
                if self.dp == 0:
                    raise ValueError("Data pointer moved to negative index")
                self.dp -= 1
            elif cmd == "+":
                # Optionally wrap at 256 if desired (i.e. emulate 8-bit cells).
                self.tape[self.dp] = (self.tape[self.dp] + 1) % 256
            elif cmd == "-":
                self.tape[self.dp] = (self.tape[self.dp] - 1) % 256
            elif cmd == ".":
                print(chr(self.tape[self.dp]), end="")
            elif cmd == "[":
                if self.tape[self.dp] == 0:
                    self.ip = self.bracket_map[self.ip]
            elif cmd == "]":
                if self.tape[self.dp] != 0:
                    self.ip = self.bracket_map[self.ip]
            else:
                raise ValueError(f"Unexpected token '{cmd}' at index {self.ip}")
            self.ip += 1

    def __str__(self) -> str:
        """
        Return a string representation of the tape and the current data pointer position.
        """
        tape_str = " ".join(map(str, self.tape))
        # Estimate pointer position based on the length of each number and a separating space.
        pointer_pos = sum(len(str(self.tape[i])) + 1 for i in range(self.dp))
        return f"\n{tape_str}\n{' ' * pointer_pos}^"

if __name__ == "__main__":
    # Example Brainfuck code.
    code = (
        "++++++++[>+++++++++<-]>.<+++++[>++++++<-]>-.+++++++..+++."
        "<++++++[>-----------<-]>-.------------.<++++++++[>+++++++++++<-]>-."
        "--------.+++.------.--------.<++++++[>-----------<-]>-.<+++++[>-----<-]>++.<"
    )
    interpreter = Brainfuck(code)
    interpreter.run()
    print(interpreter)

# Previous Code
"""
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
"""
