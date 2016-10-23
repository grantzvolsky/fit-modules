# Scala Knapsack Solver
## Coursework for the module MI-PAA [Problems and Algorithms]

Arguments
scala knapsack.jar [--mode <mode>] [--method <method>] [--itemCnt <n>]

--method <method>
	The algorithm to be used. Legal values: NaiveIteration, NaiveRecursion, NaiveRecursionSansConfigVars,
	VWRatioHeuristic and DPByCapacity.

--mode <mode>
	The <mode> value can be either "print" or "benchmark". Print will read the standard input and print
	the result of --method for each line of input.

	The benchmark uses the --itemCnt parameter to determine which benchmark to run and uses the relevant
	hardcoded input and reference file to benchmark given --method.

Examples:
cat ../input/knap_4.inst.dat | scala target/scala-2.11/knapsack_2.11-0.1-SNAPSHOT.jar --method DPByCapacity --mode print

scala target/scala-2.11/knapsack_2.11-0.1-SNAPSHOT.jar --mode benchmark --method NaiveRecursion --itemCnt 20
