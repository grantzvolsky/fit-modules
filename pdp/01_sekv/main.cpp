#include <iostream>
#include <string>

using namespace std;

class CGraph {
  int size;
  bool ** adj;
  public:
    CGraph(istream & is);
    ~CGraph();
    void print();
};

CGraph::CGraph(istream & is) {
  is >> size;
  string line;
  cin.ignore(256, '\n');
  adj = new bool*[size];
  for (int i = 0; i < size; i++) {
    adj[i] = new bool[size];
    char ch;
    for (int j = 0; j < size; j++) {
      is >> ch;
      if (ch == '0') adj[i][j] = 0;
      else if(ch == '1') adj[i][j] = 1;
      else throw "Invalid input adjacency matrix.";
    }
    is.ignore(256, '\n');
  }
  cout << line;
}

CGraph::~CGraph() {
  for (int i = 0; i < size; i++) {
    delete[] adj[i];
  }
  delete[] adj;
}

void CGraph::print() {
  for (int i = 0; i < size; i++) {
    for (int j = 0; j < size; j++) {
      cout << adj[i][j];
    } cout << endl;
  }
}

int main(int argc, char * argv[]) {
  CGraph myGraph = CGraph(cin);
  myGraph.print();
}