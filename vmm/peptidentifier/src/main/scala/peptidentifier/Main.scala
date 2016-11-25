package peptidentifier

import breeze.linalg.{max, SparseVector => SBV}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer


object Main extends App {
  println(AminoAcid.get('K'))

  val mgfLines = scala.io.Source.fromFile("/var/my_root/repos/fit/vmm/input/spectra/amethyst_annotated.mgf").getLines

  val sgrams = Spectrogram.fromMgf(mgfLines)

  import org.apache.spark.mllib.linalg.{Vector, Vectors}


  // TODO: Nebylo by lepsi stanovit nejakou deltu, ktera urci zda jsou vrcholy totozne?
  // Vysledna podobnost bude pomer suma podobnosti / mnozstvi o kolik to bylo zkraceno oproti puvodnimu vektoru
  def mergeSpectra(spectrumPeaks: List[(Double, Int)], constantIntensitySpectrum: List[Double], delta: Double): Double = {
    var product: List[Double] = Nil
    spectrumPeaks foreach { case (mz, intensity) =>
      var bIdx = 0
      while (bIdx < constantIntensitySpectrum.length) { // Toto by se dalo zoptimalizovat z O(n^2) na O(n)
        val diff = (mz - constantIntensitySpectrum(bIdx)).abs
        if (diff <= delta) product = intensity :: product
        bIdx += 1
      }
    }
    (product.sum / spectrumPeaks.map(_._2).sum) * (product.length.toDouble / spectrumPeaks.length)
  }

  // Czech original skatulkify / škatulkizace
  /*def pigeonhole(spectrum: List[(Double, Int)], magnify: Int): SparseVector = {
    val maxFragmentWeight: Double = spectrum.max._1
    val resDimension = (maxFragmentWeight * magnify).ceil.toInt
    val resElements: ListBuffer[(Int, Double)] = ListBuffer()

    val maxIntensity: Int = spectrum.map(p => p._2).max
    spectrum foreach {case (mz, intensity) =>
      val pigeonHoldIdx = (mz * magnify).toInt
      val relativeIntensity = intensity.toDouble / maxIntensity
      resElements.append((pigeonHoldIdx, relativeIntensity))
    }
    Vectors.sparse(resDimension, resElements).toSparse
  }*/

  // Czech original skatulkify / škatulkizace
  def pigeonhole(spectrum: List[(Double, Int)], magnify: Double): SBV[Double] = {
    val maxFragmentWeight: Double = spectrum.max._1
    val resDimension = (maxFragmentWeight * magnify).ceil.toInt
    val res: SBV[Double] = SBV.zeros[Double](100000)

    spectrum foreach {case (mz, intensity) =>
      val pigeonHoldIdx = (mz * magnify).toInt
      res(pigeonHoldIdx) += intensity.toDouble
    }

    val maxIntensity: Double = res.reduceLeft(_ max _)
    spectrum foreach {case (mz, intensity) =>
      val pigeonHoldIdx = (mz * magnify).toInt
      res(pigeonHoldIdx) = res(pigeonHoldIdx) / maxIntensity
    }

    res
  }

  //sgrams.head.peaks foreach {peak: (Double, Int) => spectrumVector((peak._1*10).toInt) = peak._2}
  //val sc = new SparkContext(new SparkConf().setAppName("Spark Count").setMaster("spark://troll:7077"))

  println("pigeonholed spectrum: ")
  val s0ref = pigeonhole(sgrams(0).peptide.get.ySpectrum().reverse zip (0 to sgrams.head.peptide.get.acids.length).map(_ => 1), 0.1)
  val s0 = Spectrum.clearNoise(sgrams(0).spectrum) // pigeonhole(sgrams(0).spectrum.peaks, 0.1)
  val s1ref = pigeonhole(sgrams(1).peptide.get.ySpectrum().reverse zip (0 to sgrams.head.peptide.get.acids.length).map(_ => 1), 0.1)
  val s1 = Spectrum.clearNoise(sgrams(1).spectrum)
  val s2ref = pigeonhole(sgrams(2).peptide.get.ySpectrum().reverse zip (0 to sgrams.head.peptide.get.acids.length).map(_ => 1), 0.1)
  val s2 = Spectrum.clearNoise(sgrams(2).spectrum)

  def cosim(sv1: SBV[Double], sv2: SBV[Double]): Double = {
    def euclNorm(v: SBV[Double]): Double = Math.sqrt(v.values.iterator.reduceLeft((acc, v) => acc + Math.pow(v, 2)))
    def euclDot(l: SBV[Double], r: SBV[Double]): Double = {
      val idxEnd = Math.min(sv1.length, sv2.length)
      (0 until idxEnd).foldLeft(0.0)((acc, idx) => acc + (l(idx) * r(idx)))
    }

    euclDot(sv1, sv2) / (euclNorm(sv1) * euclNorm(sv2))
  }

  //println(s"cosineSimilarity(SaS, PaP) = ${cosineSimilarity(SBV[Double](0.789, 0.515, 0.335, 0), SBV[Double](0.832, 0.555, 0, 0))}")

  /*println(s"s0ref\t= $s0ref")
  println(s"s0\t= $s0")
  println(s"s1ref\t= $s1ref")
  println(s"s1\t= $s1")
  println(s"s2ref\t= $s2ref")
  println(s"s2\t= $s2")*/

  val l = Spectrum.clearNoise(sgrams(0).spectrum)
  val map: Map[Double, Int] = (sgrams(0).peptide.get.ySpectrum().reverse zip (0 to sgrams.head.peptide.get.acids.length).map(_ => 1)).toMap
  val sp: Spectrum = Spectrum(map)
  val r = Spectrum.clearNoise(sp)

  println(f"cosim(s0ref, s0) = ${Spectrum.cosim(l, r)}%1.4f, ${mergeSpectra(sgrams(0).spectrum.peaks.toList.reverse, sgrams(0).peptide.get.ySpectrum(), 1.01)}")
  /*println(f"cosim(s0, s0ref) = ${cosim(s0, s0ref)}%1.4f, ${mergeSpectra(sgrams(0).spectrum.peaks.reverse, sgrams(0).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s1ref, s1) = ${cosim(s1ref, s1)}%1.4f, ${mergeSpectra(sgrams(1).spectrum.peaks.reverse, sgrams(1).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s1, s1ref) = ${cosim(s1, s1ref)}%1.4f, ${mergeSpectra(sgrams(1).spectrum.peaks.reverse, sgrams(1).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s2ref, s2) = ${cosim(s2ref, s2)}%1.4f, ${mergeSpectra(sgrams(2).spectrum.peaks.reverse, sgrams(2).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s2, s2ref) = ${cosim(s2, s2ref)}%1.4f, ${mergeSpectra(sgrams(2).spectrum.peaks.reverse, sgrams(2).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s0ref, s1) = ${cosim(s0ref, s1)}%1.4f, ${mergeSpectra(sgrams(1).spectrum.peaks.reverse, sgrams(0).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s1, s0ref) = ${cosim(s1, s0ref)}%1.4f, ${mergeSpectra(sgrams(1).spectrum.peaks.reverse, sgrams(0).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s1ref, s2) = ${cosim(s1ref, s2)}%1.4f, ${mergeSpectra(sgrams(2).spectrum.peaks.reverse, sgrams(1).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s2, s1ref) = ${cosim(s2, s1ref)}%1.4f, ${mergeSpectra(sgrams(2).spectrum.peaks.reverse, sgrams(1).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s2ref, s0) = ${cosim(s2ref, s0)}%1.4f, ${mergeSpectra(sgrams(0).spectrum.peaks.reverse, sgrams(2).peptide.get.ySpectrum(), 1.01)}")
  println(f"cosim(s0, s2ref) = ${cosim(s0, s2ref)}%1.4f, ${mergeSpectra(sgrams(0).spectrum.peaks.reverse, sgrams(2).peptide.get.ySpectrum(), 1.01)}")*/

  /*val builder = new CSCMatrix.Builder[Double](rows = -1, cols = 2)
  builder.add(sv1.asCscColumn)

  sv1.asCscColumn
  val newm =
  val rows: RDD[Vector] = sc.parallelize(sv1)
  val mat = new RowMatrix(rows)
  println(sv1)*/

  //println(sgrams.head.peptide.get.toString + "\n")
  /*println(sgrams.head.peptide.get.bSpectrum() + "\n")
  println(sgrams.head.peaks.reverse + "\n")

  println(mergeSpectra(sgrams.head.peaks.reverse, sgrams.head.peaks.reverse.map(p => p._1), 0.5))

  println(mergeSpectra(sgrams.head.peaks.reverse, sgrams.head.peptide.get.ySpectrum(), 0.5))
  println(mergeSpectra(sgrams.head.peaks.reverse, sgrams.head.peptide.get.bSpectrum(), 0.5))*/

  /*val referenceSpectrumVector = SparseVector.zeros[Int](40000)
  sgrams.head.peptide.get.acids

  sgrams.head.peptide match {
    case Some(peptide) =>
      println(peptide.toString)
      sgrams.head.peaks foreach {peak: (Double, Int) => sv((peak._1*10).toInt) = peak._2}
    case None => println("Spectrum has no reference peptide.")
  }*/

}