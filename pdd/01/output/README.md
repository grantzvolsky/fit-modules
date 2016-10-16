Test setup:
* Ubuntu 16.04
* Intel(R) Core(TM) i7-4700HQ CPU @ 2.40GHz

```
# Run naive
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_10.inst.dat > output/naive/knap_10.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_15.inst.dat > output/naive/knap_15.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_25.inst.dat > output/naive/knap_25.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_20.inst.dat > output/naive/knap_20.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_22.inst.dat > output/naive/knap_22.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_30.inst.dat > output/naive/knap_30.heuristic.out

# Run with heuristic
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_10.inst.dat > output/heuristic/knap_10.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_15.inst.dat > output/heuristic/knap_15.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_20.inst.dat > output/heuristic/knap_20.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_22.inst.dat > output/heuristic/knap_22.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_25.inst.dat > output/heuristic/knap_25.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_30.inst.dat > output/heuristic/knap_30.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_35.inst.dat > output/heuristic/knap_35.heuristic.out
scala target/scala-2.10/knapsack_2.10-0.1-SNAPSHOT.jar -h -Dscala.time < input/knap_40.inst.dat > output/heuristic/knap_40.heuristic.out

# Calculate relative error

readarray -t OPT_VALUES_ARR <<< `cat reference/knap_40.sol.dat | cut -d" " -f3`
readarray -t APX_VALUES_ARR <<< `cat heuristic/knap_40.heuristic.out | head -n -1 | cut -d" " -f3`

for idx in ${!OPT_VALUES_ARR[@]}
do
    V_OPT=${OPT_VALUES_ARR[idx]}
    V_APX=${APX_VALUES_ARR[idx]}
    echo "($V_OPT - $V_APX) / $V_OPT" | bc -l
done
```