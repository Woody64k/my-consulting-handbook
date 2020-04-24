// Fold
let sum = my_numbers => {
	List.fold_left(
		(a,b) => a + b,
		0,
		my_numbers
	)
}

// Currying
let lessThen1000

// Map
let my_int_of_string = str =>
	 switch (str) {
	| "" => 0
	| str => int_of_string(str)
	}

let mapToInt = List.map(my_int_of_string); 


// main
let stringCalc = str => sum(lessThen1000(mapToInt(splitByComma(str))));

let stringCalc = str => str
	|> splitByComma
	|> mapToInt
	|> lessThen1000
	|> sum;