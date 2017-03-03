#include <iostream>
#include "CGraph.cpp"

int main(int argc, char * argv[]) {
  CGraph myGraph = CGraph(std::cin);
  myGraph.print();
}