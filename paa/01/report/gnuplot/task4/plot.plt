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
set xlabel "constants"
set ylabel "average time [microseconds]"

set y2tics nomirror

set yrange [0:]
set y2range [0:]

set key left
set key above

set output './out.pdf'
plot "<cat pdf_evolution/const.csv | grep ',Evolution,' " using 2:7:xtic(2) title "average time", \
     "<cat pdf_evolution/const.csv | grep ',Evolution,' " using 2:8:xtic(2) title "average relative error" axes x1y2, \
     "<cat pdf_evolution/const.csv | grep ',Evolution,' " using 2:9:xtic(2) title "maximum relative error" axes x1y2

#set output '../pdf/BranchAndBound.pdf'
#set logscale
#plot "<cat 2d.csv | tail -n +1 | grep '^BranchAndBound,'" using 2:3:xtic(2) title "BranchAndBound O(n^2)", \
#     "<cat 2d.csv | tail -n +1 | grep '^BranchAndBound,'" using 2:(0.002*(2)**(0.9*$2)+400) title "0.002*2^{0.9n}+400" with lines
#unset logscale
