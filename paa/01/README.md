# Scala Knapsack Solver
## Coursework for the module MI-PAA [Problems and Algorithms]

Arguments
scala knapsack.jar [-Dmode="<mode>"] [-Dmethod="<method>"] [-DitemCnt="<n>"]

-Dmethod="<method>"
	The algorithm to be used. Legal values: NaiveIteration, NaiveRecursion, NaiveRecursionSansConfigVars,
	VWRatioHeuristic and DPByCapacity.

-Dmode="<mode>"
	The <mode> value can be either "print" or "benchmark". Print will read the standard input and print
	the result of -Dmethod for each line of input.

	The benchmark uses the -DitemCnt parameter to determine which benchmark to run and uses the relevant
	hardcoded input and reference file to benchmark given -Dmethod.

Examples:
cat ../input/knap_4.inst.dat | scala target/scala-2.11/knapsack_2.11-0.1-SNAPSHOT.jar -Dmethod="DPByCapacity" -Dmode="print"

scala target/scala-2.11/knapsack_2.11-0.1-SNAPSHOT.jar -Dmode="benchmark" -Dmethod="NaiveRecursion" -DitemCnt="20"
