#  scatter_3d_input.txt
#
#  splot reference:
#    https://www.physics.ohio-state.edu/~ntg/6810/handouts/gnuplot_3d_example_v3.pdf
#    http://stackoverflow.com/questions/16344044/remove-empty-space-in-splot-with-log-scaled-z-axis
#


set xlabel "instance size"
set ylabel "epsilon"
set zlabel "average time [microseconds]" offset 6,3

set ytics (0.25,0.20,0.15,0.10,0.05,0.01)
set xyplane 0.2
splot 'FPTAS_data.txt' using 1:2:3:xtic(1)
