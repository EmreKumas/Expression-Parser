# Expression-Parser
This parser converts one-line expression into ***assembly language*** format using the current ISA. It takes an input that consists of ***arithmetic*** and ***logic*** expressions and generate the corresponding assembly code.

## Implementation Details

An example input could be like this: `[(((117|64)++)-(47&15))--]^!(12-95)`

Then the program converts this expression into **postfix form** to be able to process it more easily. Then it converts it into a **binary tree** and compute the result to derive the final solution.

One of the crucial tasks was to track the registers. Because we have only four registers, we need to keep track of each operation and empty the registers that we don't need anymore.

### Example Input

`[(((117|64)++)-(47&15))--]^!(12-95)`

### Example Output

<pre>
ldi 0 117
ldi 1 64
or 2 0 1
inc 2
ldi 0 47
ldi 1 15
and 3 0 1
sub 0 2 3
dec 0
ldi 2 12
ldi 3 95
sub 1 2 3
not 1 1
xor 0 0 1
</pre>
