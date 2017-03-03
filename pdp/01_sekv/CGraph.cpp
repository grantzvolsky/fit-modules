class CGraph {
  int size;
  bool ** adj;
public:
  CGraph(std::istream & is);
  ~CGraph();
  void print();
};

CGraph::CGraph(std::istream & is) {
  is >> size;
  std::cin.ignore(256, '\n');
  adj = new bool*[size];
  for (int i = 0; i < size; i++) {
    adj[i] = new bool[size];
    char ch;
    for (int j = 0; j < size; j++) {
      is >> ch;
      if (is.fail()) throw "Invalid input.";
      if (ch == '0') adj[i][j] = 0;
      else if(ch == '1') adj[i][j] = 1;
      else throw "Invalid input.";
    }
    is.ignore(256, '\n');
  }
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
      std::cout << adj[i][j];
    } std::cout << std::endl;
  }
}