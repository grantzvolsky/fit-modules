package knapsack.solvers

import scala.collection.immutable.BitSet
import scala.collection.mutable
import scala.collection.breakOut

object Evolution extends KnapsackSolver {
  case class Individual(genotype: BitSet, fitness: Int) extends Ordered[Individual] {
    def mutatedGenotype(maxBit: Int): BitSet = {
      val randomBit = scala.util.Random.nextInt(maxBit)
      if (genotype(randomBit)) genotype - randomBit
      else genotype + randomBit
    }
    def compare(that: Individual): Int = that.fitness compare this.fitness
  }

  var populationSize = 50
  var maxGenerations = 50
  var polygamyRate = 10

  def variableMaxGenerations(maxGens: Int): KnapsackSolver = {
    Evolution.maxGenerations = maxGens
    this
  }

  def variablePolygamy(polygamy: Int): KnapsackSolver = {
    Evolution.polygamyRate = polygamy
    this
  }

  def variablePopulationSize(popSize: Int): KnapsackSolver = {
    Evolution.populationSize = popSize
    this
  }

  def const(v: Int): KnapsackSolver = {
    this
  }

  override def solve(items: Array[(Int, Int)], capacity: Int): Configuration = {
    def fitness(genotype: BitSet): Int = {
      var accW, accV = 0
      genotype foreach {idx =>
        accW += items(idx)._1
        if (accW <= capacity) accV += items(idx)._2 // Don't count items that don't fit.
      }
      accV
    }

    def fitness2(genotype: BitSet): Int = {
      val itms: Array[(Int, Int)] = genotype.toArray map (idx => items(idx))
      VWRatioHeuristic.solve(itms, capacity).value
    }

    def basicPopulation(k: Int): IndexedSeq[Individual] = {
      (1 to k).flatMap { n =>
        items.indices.combinations(n) map { individual: IndexedSeq[Int] =>
          val genotype: BitSet = individual.map(identity)(breakOut)
          Individual(genotype, fitness(genotype))
        }
      }
    }

    def randomPopulation(n: Int, density: Double = 0.2): IndexedSeq[Individual] = {
      (0 until n).map { ignored: Int =>
        var genotype: BitSet = BitSet.empty
        items.indices foreach (i => if (Math.random() < density) genotype = genotype + i)
        Individual(genotype, fitness(genotype))
      }
    }

    def crossover(mother: Individual, father: Individual): IndexedSeq[Individual] = {
      val intersection = scala.util.Random.nextInt(items.length)
      val g1 = mother.genotype.filter(_ <= intersection) | father.genotype.filter(_ > intersection)
      val g2 = mother.genotype.filter(_ > intersection) | father.genotype.filter(_ <= intersection)
      val firstBorn = Individual(g1, fitness(g1))
      val secondBorn = Individual(g2, fitness(g2))
      IndexedSeq(firstBorn, secondBorn)
    }

    def validate(individual: BitSet): Configuration = {
      var validW, validV, accW = 0
      val validIndividual: mutable.BitSet = mutable.BitSet.empty
      individual foreach {idx =>
        accW += items(idx)._1
        if (accW <= capacity) {
          validV += items(idx)._2
          validW = accW
          validIndividual += idx
        } // Don't count items that don't fit.
      }
      Configuration(validW, validV, validIndividual)
    }

    def reproduce(individuals: IndexedSeq[Individual]): IndexedSeq[Individual] = {
      individuals.indices.flatMap { idx =>
        val crossoverProbability = 0.3 + 2/(idx+1)
        if (Math.random() < crossoverProbability) crossover(individuals(idx), individuals(scala.util.Random.nextInt(individuals.length)))
        else Nil
      }
    }

    def reproducePolygamous(individuals: IndexedSeq[Individual], polygamy: Int): IndexedSeq[Individual] = {
      individuals.indices.flatMap { maleIdx =>
        (0 until polygamy).flatMap { k =>
          val femaleIdx = (maleIdx + k) % individuals.length
          crossover(individuals(maleIdx), individuals(femaleIdx))
        }
      }
    }

    def mutate(individuals: IndexedSeq[Individual]): IndexedSeq[Individual] = individuals map { i =>
      val mutatedGenotype = i.mutatedGenotype(items.length)
      Individual(mutatedGenotype, fitness(mutatedGenotype))
    }


    def evolution1(size: Int, generations: Int, polygamy: Int): Configuration = {
      var individuals = (basicPopulation(1).sorted.take(size) ++ randomPopulation(size, 0.1)).sorted

      (1 to generations) foreach { ignored =>
        val offspring = reproducePolygamous(individuals, polygamy)
        val mutatedOffspring = mutate(offspring) ++ mutate(mutate(offspring)) ++ mutate(mutate(mutate(offspring)))
        individuals = (individuals ++ offspring ++ mutatedOffspring).sorted.take(individuals.length) //(worthy ++ offspring).sorted.take(population.length)
      }
      validate(individuals.head.genotype)
      //val itms: Array[(Int, Int)] = individuals.head.genotype.toArray map (idx => items(idx))
      //VWRatioHeuristic.solve(itms, capacity)
    }

    evolution1(populationSize, maxGenerations, polygamyRate)
  }

  override def toString = "Evolution"
}
