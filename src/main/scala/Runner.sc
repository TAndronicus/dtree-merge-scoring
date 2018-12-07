import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.NGram
import org.apache.spark.sql.SparkSession

var conf = new SparkConf().setAppName("dtree-merge").setMaster("local")
var spark = SparkSession.builder().config(conf).getOrCreate()
var dataFrame = spark.createDataFrame(Seq(
  (0, Array("Hi", "I", "heard", "about", "Spark")),
  (1, Array("I", "wish", "Java", "could", "use", "case", "classes")),
  (2, Array("Logistic", "regression", "models", "are", "neat"))
)).toDF("id", "words")

var ngram = new NGram().setN(2).setInputCol("words").setOutputCol("ngrams")

var ngramDataFrame = ngram.transform(dataFrame)
ngramDataFrame.select("ngrams").show(false)
