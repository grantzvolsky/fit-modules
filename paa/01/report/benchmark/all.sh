BIN_PATH="/var/my_root/repos/fit/paa/01/solution/target/scala-2.11/knapsack_2.11-0.1-SNAPSHOT.jar"

METHOD="NaiveIteration"

echo "METHOD INSTANCE_SIZE TIME_PER_INSTANCE_MICROS AVG_REL_ERR MAX_REL_ERR EPSILON"
for INSTANCE_SIZE in 4 10 15 20 22 25 27 30 32
do
  scala $BIN_PATH -Dmode="benchmark" -Dmethod=$METHOD -DitemCnt=$INSTANCE_SIZE -Depsilon="0.0"
done

