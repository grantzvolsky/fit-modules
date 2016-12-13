#  scatter_3d_input.txt
#
#  splot reference:
#    https://www.physics.ohio-state.edu/~ntg/6810/handouts/gnuplot_3d_example_v3.pdf
#    http://stackoverflow.com/questions/16344044/remove-empty-space-in-splot-with-log-scaled-z-axis
#

set terminal pdf size 4.5,4.5
set key font ",11"
set xtics font ",9"
set xtics nomirror
set ytics nomirror
set pointsize 1.2

set datafile separator ","
set xlabel "instance size"
set ylabel "average time [microseconds]" offset 12,3

set output '../pdf/DPByValue.pdf'
plot "<cat 2d.csv | tail -n +1 | grep 'DPByValue'" using 2:3:xtic(2) title "DPByValue O(T*n)"

set output '../pdf/DPByCapacity.pdf'
set key left
plot "<cat 2d.csv | tail -n +1 | grep '^DPByCapacity,'" using 2:3:xtic(2) title "DPByCapacity O(C*n)"

set output '../pdf/NaiveRecursion.pdf'
set logscale
plot "<cat 2d.csv | tail -n +1 | grep '^NaiveRecursionSansConfigVars,'" using 2:3:xtic(2) title "NaiveRecursion O(2^n)", \
     "<cat 2d.csv | tail -n +1 | grep '^NaiveRecursionSansConfigVars,'" using 2:(0.0071002*(2)**(0.993105*$2)+300) title "0.007*2^n+300" with lines
unset logscale

set output '../pdf/NaiveIteration.pdf'
set logscale
plot "<cat 2d.csv | tail -n +1 | grep '^NaiveIteration,'" using 2:3:xtic(2) title "NaiveIteration O(n*2^n)", \
     "<cat 2d.csv | tail -n +1 | grep '^NaiveIteration,'" using 2:(0.3*2**$2+300) title "0.3n*2^n+300" with lines

unset logscale

set output '../pdf/VWRatioHeuristic.pdf'
plot "<cat 2d.csv | tail -n +1 | grep '^VWRatioHeuristic,'" using 2:3:xtic(2) title "VWRatioHeuristic O(n*log(n))"

set output '../pdf/BranchAndBound.pdf'
set logscale
plot "<cat 2d.csv | tail -n +1 | grep '^BranchAndBound,'" using 2:3:xtic(2) title "BranchAndBound O(n^2)", \
     "<cat 2d.csv | tail -n +1 | grep '^BranchAndBound,'" using 2:(0.002*(2)**(0.9*$2)+400) title "0.002*2^{0.9n}+400" with lines
unset logscale

set output '../pdf/FPTAS.pdf'
plot "<cat 2d.csv | tail -n +1 | grep '^FPTAS,'" using 2:3:xtic(2)
